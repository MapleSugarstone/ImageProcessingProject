package lambda;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import saaf.Inspector;
import saaf.Response;

import utils.GaussianFilter;

/**
 * uwt.lambda_test::handleRequest
 *
 * @author Wes Lloyd
 * @author Robert Cordingly
 */
public class ProcessImage implements RequestHandler<Request, HashMap<String, Object>> {

	static BufferedImage bf = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	static String loadedFilterType = "unassigned";
    /**
     * Lambda Function Handler
     * 
     * @param request Hashmap containing request JSON attributes.
     * @param context 
     * @return HashMap that Lambda will automatically convert into JSON.
     */
    public HashMap<String, Object> handleRequest(Request request, Context context) {
        
        //Collect initial data.
        Inspector inspector = new Inspector();
        inspector.inspectCPU();
        
        //****************START FUNCTION IMPLEMENTATION*************************
		
		String bucketname = request.getBucketname();
		String filename = request.getFilename();
		String filterType = request.getFilterType();
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
		GetObjectRequest s3Request = new GetObjectRequest(bucketname, filename);
		S3Object s3Obj = s3Client.getObject(s3Request);
		try {
			if (bf.getHeight() == 1 || loadedFilterType.compareTo(filterType) != 0) {
				bf = ImageIO.read(s3Obj.getObjectContent());
				loadedFilterType = filterType;
			}
			switch (filterType) {
				case "gaussian":
					GaussianFilter gf = new GaussianFilter(10);
					BufferedImage bfGauss = gf.filter(bf, null);
					ByteArrayOutputStream os3 = new ByteArrayOutputStream();
					ImageIO.write(bfGauss, "png", os3);
					InputStream is3 = new ByteArrayInputStream(os3.toByteArray());
					ObjectMetadata meta3 = new ObjectMetadata();
					meta3.setContentLength(os3.size());
					meta3.setContentType("image/png");
					s3Client.putObject(bucketname, "outgauss.png", is3, meta3);
					break;
				case "greyscale":
					BufferedImage bfTempGrey = new BufferedImage(bf.getWidth(), bf.getHeight(), bf.getType());
					for (int i = 0; i < bf.getHeight(); i++) {
						for (int j = 0; j < bf.getWidth(); j++) {
							int initial = bf.getRGB(j, i);
							int grey = (int) (0.299 * (initial >> 16 & 0xFF) + 0.587 * (initial >> 8 & 0xFF) + 0.144 * (initial & 0xFF));
							bfTempGrey.setRGB(j, i, (grey << 16) | (grey << 8) | (grey));
						}
					}
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(bfTempGrey, "png", os);
					InputStream is = new ByteArrayInputStream(os.toByteArray());
					ObjectMetadata meta = new ObjectMetadata();
					meta.setContentLength(os.size());
					meta.setContentType("image/png");
					s3Client.putObject(bucketname, "outgrey.png", is, meta);
					break;

				case "sepia":
					BufferedImage bfTempSepia = new BufferedImage(bf.getWidth(), bf.getHeight(), bf.getType());
					for (int i = 0; i < bf.getHeight(); i++) {
						for (int j = 0; j < bf.getWidth(); j++) {
							int initial = bf.getRGB(j, i);
							int red = (initial >> 16) & 0xFF;
							int green = (initial >> 8) & 0xFF;
							int blue = initial & 0xFF;

							int sepiaRed = (int) (red * 0.393 + green * 0.769 + blue * 0.189);
							int sepiaGreen = (int) (red * 0.349 + green * 0.686 + blue * 0.168);
							int sepiaBlue = (int) (red * 0.272 + green * 0.534 + blue * 0.131);

							sepiaRed = Math.min(sepiaRed, 255);
							sepiaGreen = Math.min(sepiaGreen, 255);
							sepiaBlue = Math.min(sepiaBlue, 255);
							bfTempSepia.setRGB(j, i, (sepiaRed << 16) | (sepiaGreen << 8) | sepiaBlue);
						}
					}
					ByteArrayOutputStream os2 = new ByteArrayOutputStream();
					ImageIO.write(bfTempSepia, "png", os2);
					InputStream is2 = new ByteArrayInputStream(os2.toByteArray());
					ObjectMetadata meta2 = new ObjectMetadata();
					meta2.setContentLength(os2.size());
					meta2.setContentType("image/png");
					s3Client.putObject(bucketname, "outsepia.png", is2, meta2);

					break;

				default:
					System.err.print("Filter must be one of \"gaussian\", \"greyscale\", or \"sepia\".");
					break;
			}
			// ByteArrayOutputStream os = new ByteArrayOutputStream();
			// ImageIO.write(bf, "png", os);
			// InputStream is = new ByteArrayInputStream(os.toByteArray());
			// ObjectMetadata meta = new ObjectMetadata();
			// meta.setContentLength(os.size());
			// meta.setContentType("image/png");
			// s3Client.putObject(bucketname, "out.png", is, meta);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
        
        
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response response = new Response();
        response.setValue("Bucket:" + bucketname + " filename:" + filename);
		
        
        inspector.consumeResponse(response);
        
        //****************END FUNCTION IMPLEMENTATION***************************
                
        //Collect final information such as total runtime and cpu deltas.
        inspector.inspectCPUDelta();
		inspector.inspectContainer();
		inspector.inspectPlatform();  
		return inspector.finish();
    }
}

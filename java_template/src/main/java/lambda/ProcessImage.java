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
	static String imageName = "none";

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
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
		GetObjectRequest s3Request = new GetObjectRequest(bucketname, filename);
		S3Object s3Obj = s3Client.getObject(s3Request);
		try {
			if (bf.getHeight() == 1 || imageName.compareTo(filename) != 0) {
				bf = ImageIO.read(s3Obj.getObjectContent());
				imageName = filename;
			}

			BufferedImage bfOut = new BufferedImage(bf.getHeight(), bf.getWidth(), bf.getType());
			for (int i = 0; i < bf.getHeight(); i++) {
				for (int j = 0; j < bf.getWidth(); j++) {
					bfOut.setRGB(i, j, bf.getRGB(j, i));
				}
			}
			GaussianFilter gf = new GaussianFilter(10);
			gf.filter(bfOut, bfOut);

			for (int i = 0; i < bfOut.getHeight(); i++) {
				for (int j = 0; j < bfOut.getWidth(); j++) {
					int initial = bfOut.getRGB(j, i);
					int alpha = (initial >> 24) & 0xFF;
					int red = (initial >> 16) & 0xFF;
					int green = (initial >> 8) & 0xFF;
					int blue = initial & 0xFF;

					int sepiaRed = (int) (red * 0.393 + green * 0.769 + blue * 0.189);
					int sepiaGreen = (int) (red * 0.349 + green * 0.686 + blue * 0.168);
					int sepiaBlue = (int) (red * 0.272 + green * 0.534 + blue * 0.131);

					sepiaRed = Math.min(sepiaRed, 255);
					sepiaGreen = Math.min(sepiaGreen, 255);
					sepiaBlue = Math.min(sepiaBlue, 255);
					bfOut.setRGB(j, i, (alpha << 24) | (sepiaRed << 16) | (sepiaGreen << 8) | sepiaBlue);
				}
			}
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bfOut, "png", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(os.size());
			meta.setContentType("image/png");
			s3Client.putObject(bucketname, "out.png", is, meta);
			bfOut.flush();
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

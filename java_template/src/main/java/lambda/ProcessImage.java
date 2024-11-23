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

/**
 * uwt.lambda_test::handleRequest
 *
 * @author Wes Lloyd
 * @author Robert Cordingly
 */
public class ProcessImage implements RequestHandler<Request, HashMap<String, Object>> {

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
			BufferedImage bf = ImageIO.read(s3Obj.getObjectContent());
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bf, "png", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(os.size());
			meta.setContentType("image/png");
			s3Client.putObject(bucketname, "out.png", is, meta);
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

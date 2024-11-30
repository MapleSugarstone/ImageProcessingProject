import boto3
from io import BytesIO
from PIL import Image, ImageFilter
import json
import logging
from Inspector import Inspector
import time


# cloud_function(platforms=[Platform.AWS], memory=512, config=config)
def handle_request(request):
    bucketname = str(request["bucketname"])
    filename = str(request["filename"])

    s3_client = boto3.client("s3")

    try:
        # Get the image from S3
        s3_obj = s3_client.get_object(Bucket=bucketname, Key=filename)
        image_data = s3_obj["Body"].read()
        bf = Image.open(BytesIO(image_data))

        # Apply Gaussian filter
        bf_out = bf.transpose(Image.Transpose.ROTATE_90)  # Rotate image
        bf_out = bf_out.filter(ImageFilter.GaussianBlur(radius=10))

        # Apply sepia filter
        bf_out = apply_sepia(bf_out)

        # Save the output image to a BytesIO object
        output_buffer = BytesIO()
        bf_out.save(output_buffer, format="PNG")
        output_buffer.seek(0)

        # Upload the processed image back to S3
        s3_client.put_object(
            Bucket=bucketname,
            Key="py-out.png",
            Body=output_buffer,
            ContentType="image/png",
        )

    except Exception as e:
        print(f"Error processing image: {e}")
        return {"error": str(e)}

    # Import the module and collect data
    inspector = Inspector()
    inspector.inspectCPU()

    # Add custom message and finish the function
    inspector.addAttribute("bucketname", str(request["bucketname"]))
    inspector.addAttribute("filename", str(request["filename"]))

    inspector.inspectCPUDelta()
    inspector.inspectContainer()
    inspector.inspectPlatform()
    return inspector.finish()


def apply_sepia(image):
    width, height = image.size
    pixels = image.load()  # Create the pixel map

    for py in range(height):
        for px in range(width):
            r, g, b = image.getpixel((px, py))

            # Apply sepia filter
            sepia_red = int(r * 0.393 + g * 0.769 + b * 0.189)
            sepia_green = int(r * 0.349 + g * 0.686 + b * 0.168)
            sepia_blue = int(r * 0.272 + g * 0.534 + b * 0.131)

            # Clamp values to 255
            sepia_red = min(sepia_red, 255)
            sepia_green = min(sepia_green, 255)
            sepia_blue = min(sepia_blue, 255)

            # Update the pixel
            pixels[px, py] = (sepia_red, sepia_green, sepia_blue)

    return image

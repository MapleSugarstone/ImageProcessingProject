package lambda;

/**
 *
 * @author Wes Lloyd
 */
public class Request {

    String name;
	private String bucketname;
	private String filename;

    public String getName() {
        return name;
    }
    
    public String getNameALLCAPS() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getBucketname() {
		return bucketname;
	}

	public void setBucketname(String bucketname) {
		this.bucketname = bucketname;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

    public Request(String name) {
        this.name = name;
    }

    public Request() {

    }
}

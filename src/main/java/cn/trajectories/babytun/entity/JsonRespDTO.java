package cn.trajectories.babytun.entity;

public class JsonRespDTO<T> {
	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAILURE = "failed";
	public static final String STATUS_ERROR = "error";
	public static final String STATUS_EXPIRE = "expire";
    public static final String STATUS_REMIND = "remind";
    public static final String STATUS_LOGOUT = "logout";

	private String status = STATUS_SUCCESS;

	private String message;

	private T data;

	public JsonRespDTO() {
	}

	public JsonRespDTO(String message, T data) {
		this.message = message;
		this.data = data;
	}

	public JsonRespDTO(String status, String message, T data) {
		this.status = status;
		this.message = message;
		this.data = data;
	}

	public JsonRespDTO(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public JsonRespDTO(T data) {
		this.data = data;
	}

	public static JsonRespDTO success(Object data){
		return new JsonRespDTO(data);
	}
	public static JsonRespDTO success(String message,Object data){
		return new JsonRespDTO(message,data);
	}

	public static JsonRespDTO error(String message){
		return new JsonRespDTO(STATUS_ERROR,message);
	}

	public static JsonRespDTO fail(String message){
		return new JsonRespDTO(STATUS_FAILURE,message);
	}

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}

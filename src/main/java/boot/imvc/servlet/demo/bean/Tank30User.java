package boot.imvc.servlet.demo.bean;

import java.io.Serializable;
import java.util.Date;

public class Tank30User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String uuid;
	private Long sort;
	private Date update_time;
	private Date create_time;
	private String role;
	private String username;
	private String password;
	private String avatar_url;
	private String last_ip;
	private Date last_time;
	private Long size_limit;
	private Long total_size_limit;
	private Long total_size;
	private String status;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Long getSort() {
		return sort;
	}
	public void setSort(Long sort) {
		this.sort = sort;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAvatar_url() {
		return avatar_url;
	}
	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}
	public String getLast_ip() {
		return last_ip;
	}
	public void setLast_ip(String last_ip) {
		this.last_ip = last_ip;
	}
	public Date getLast_time() {
		return last_time;
	}
	public void setLast_time(Date last_time) {
		this.last_time = last_time;
	}
	public Long getSize_limit() {
		return size_limit;
	}
	public void setSize_limit(Long size_limit) {
		this.size_limit = size_limit;
	}
	public Long getTotal_size_limit() {
		return total_size_limit;
	}
	public void setTotal_size_limit(Long total_size_limit) {
		this.total_size_limit = total_size_limit;
	}
	public Long getTotal_size() {
		return total_size;
	}
	public void setTotal_size(Long total_size) {
		this.total_size = total_size;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}


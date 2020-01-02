package com.socure.treasurehunt.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	private String name;

	@Column(unique = true)
	@NotNull
	private String loginName;

	@Column
	private String password;

	@Column
	private String phone;

	@Column(unique = true)
	@NotNull
	private String email;

	@Column
	private String institution;

	@Column(unique = true)
	private String token;

	@Column
	private Integer level = 0;

	@Column
	private String stats = "";

	@Column
	private String currentQuestion = "";
	
	@Column
	private Boolean isBanned = false;
	
	@Column 
	private String currentClue = "";
	
	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Metric> metrics;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getLevel() {
		return level;
	}
	
	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getStats() {
		return stats;
	}

	public void setStats(String stats) {
		this.stats = stats;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getCurrentQuestion() {
		return currentQuestion;
	}

	public void setCurrentQuestion(String currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public Boolean getIsBanned() {
		return isBanned;
	}

	public void setIsBanned(Boolean isBanned) {
		this.isBanned = isBanned;
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public String getCurrentClue() {
		return currentClue;
	}

	public void setCurrentClue(String currentClue) {
		this.currentClue = currentClue;
	}
}

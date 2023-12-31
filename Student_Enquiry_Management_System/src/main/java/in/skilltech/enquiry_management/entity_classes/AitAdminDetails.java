package in.skilltech.enquiry_management.entity_classes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class AitAdminDetails {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer adminId;
	
	private String adminEmail;
	
	private String adminPassword;

}

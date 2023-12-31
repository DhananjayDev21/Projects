package in.skilltech.enquiry_management.services.impl;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.skilltech.enquiry_management.bind.LoginForm;
import in.skilltech.enquiry_management.bind.SignupForm;
import in.skilltech.enquiry_management.bind.UnlockForm;
import in.skilltech.enquiry_management.constants.AppConstant;
import in.skilltech.enquiry_management.entity_classes.AitUserDetails;
import in.skilltech.enquiry_management.repositories.UserDtlsRepo;
import in.skilltech.enquiry_management.services.UserService;
import in.skilltech.enquiry_management.utils.EmailUtil;
import in.skilltech.enquiry_management.utils.PwdUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDtlsRepo repo;

	@Autowired
	private EmailUtil emailUtil;

	@Autowired
	private HttpSession session;

	@Override
	public boolean signUp(SignupForm form) {

		AitUserDetails email = repo.findByUserEmail(form.getUserEmail());
		AitUserDetails phono = repo.findByUserPhonNo(form.getUserPhonNo());

		if (phono != null || email != null) {

			return false;
		}

		AitUserDetails entity = new AitUserDetails();
		BeanUtils.copyProperties(form, entity);


		String tempPwd = PwdUtils.generateRandomPwd();
		entity.setPassWord(tempPwd);

		entity.setAccStatus("Locked");

		repo.save(entity);


		String to = form.getUserEmail();

		String subject = AppConstant.EMAIL_UNL_MSG;


		StringBuilder body = new StringBuilder("");

		body.append(AppConstant.EMAIL_MSG_1);

		body.append(AppConstant.EMAIL_MSG_2 + tempPwd);

		body.append("<br/>");

		body.append(AppConstant.EMAIL_MSG_3 + to + AppConstant.EMAIL_MSG_4);

		emailUtil.sendEmail(to, subject, body.toString());

		return true;
	}

	public boolean unlockAccount(UnlockForm form) {

		AitUserDetails entity = repo.findByUserEmail(form.getEmail());

		if (entity.getPassWord().equals(form.getTempPwd())) {
			entity.setPassWord(form.getNewPwd());
			entity.setAccStatus("Unlocked");

			repo.save(entity);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String login(LoginForm form) {

		String pwd = form.getPwd();

		AitUserDetails result = repo.findByUserEmailAndPassWord(form.getEmail(), pwd);

		if (result == null) {
			return AppConstant.INVALID_CRED;
		}
		if (result.getAccStatus().equals("Locked")) {
			return AppConstant.ACC_LOCKED;
		}

		session.setAttribute("userId", result.getUserId());
		return "Success";

	}

	@Override
	public boolean emailForForgot(String email) {

		AitUserDetails entity = repo.findByUserEmail(email);
		if (entity == null) {

			return false;
		}
		String subject = AppConstant.RECOVER_PD;
		String body = AppConstant.YOUR_PD + entity.getPassWord();
		emailUtil.sendEmail(email, subject, body);

		return true;
	}

}

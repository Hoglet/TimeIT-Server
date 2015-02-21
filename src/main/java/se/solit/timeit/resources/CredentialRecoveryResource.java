package se.solit.timeit.resources;

import io.dropwizard.jersey.caching.CacheControl;
import io.dropwizard.jersey.sessions.Session;
import io.dropwizard.views.View;

import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.solit.timeit.application.MailerInterface;
import se.solit.timeit.dao.LoginKeyDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.LoginKey;
import se.solit.timeit.entities.User;
import se.solit.timeit.utilities.Email;
import se.solit.timeit.views.ChangePasswordView;
import se.solit.timeit.views.RecoverView;

import com.sun.jersey.api.core.HttpContext;

@Path("/recover")
public class CredentialRecoveryResource extends BaseResource
{
	private static final int		DAY_LIMIT	= 3;
	private final MailerInterface	mailer;
	private final UserDAO			userDAO;
	private final LoginKeyDAO		loginKeyDAO;
	private static final Logger		LOGGER		= LoggerFactory.getLogger(CredentialRecoveryResource.class);

	public CredentialRecoveryResource(EntityManagerFactory emf, MailerInterface mailer)
	{
		this.mailer = mailer;
		userDAO = new UserDAO(emf);
		loginKeyDAO = new LoginKeyDAO(emf);
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@CacheControl(maxAge = 15, maxAgeUnit = TimeUnit.MINUTES)
	public View recoverPage(@Context HttpContext context,
			@Session HttpSession session)
	{
		return new RecoverView(context, session);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	public View requestCredentials(@Context HttpContext context,
			@Session HttpSession session, @FormParam("address") String mailaddress) throws URISyntaxException
	{

		try
		{
			User user = userDAO.getByEMail(mailaddress);
			if (user == null)
			{
				LOGGER.error("Request credential failed, no user with that mail address");
				setMessage(session, "No user connected to " + mailaddress);
			}
			else
			{

				String subject = "Request for new password";
				InternetAddress recipient = new InternetAddress(mailaddress);

				LoginKey loginKey = new LoginKey(user);
				loginKeyDAO.add(loginKey);

				StringBuilder sb = new StringBuilder();
				sb.append("Goto link : ");
				sb.append(context.getUriInfo().getBaseUri());
				sb.append("recover/" + loginKey.getId());
				sb.append("\n");
				String message = sb.toString();

				Email email = new Email(recipient, subject, message);
				mailer.sendMail(email);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Request credential failed", e);
			setMessage(session, "Failed to send message. Error message: \"" + e.getMessage() + "\"");
			throw redirect("/recover");
		}
		throw redirect("/");
	}

	@GET
	@Path("/{keyid}")
	@Produces("text/html;charset=UTF-8")
	public View changePassword(@Context HttpContext context,
			@Session HttpSession session, @PathParam("keyid") String keyid) throws URISyntaxException
	{
		loginKeyDAO.removeOld(Duration.standardDays(DAY_LIMIT));
		LoginKey loginKey = loginKeyDAO.getByID(UUID.fromString(keyid));
		if (loginKey == null)
		{
			setMessage(session, "This key is no longer active, please do a new request");
			throw redirect("/");
		}
		return new ChangePasswordView(loginKey.getUser(), keyid, context, session);
	}

	@POST
	@Path("/{keyid}")
	@Produces("text/html;charset=UTF-8")
	public void changePasswordPost(@PathParam("keyid") String keyid, @FormParam("password") String newPassword)
			throws URISyntaxException
	{
		LoginKey loginKey = loginKeyDAO.getByID(UUID.fromString(keyid));
		User user = loginKey.getUser();
		user.setPassword(newPassword);
		userDAO.update(user);
		loginKeyDAO.delete(loginKey);
		throw redirect("/");
	}

}

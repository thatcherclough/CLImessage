package dev.thatcherclough.climessage;

import javax.mail.search.FlagTerm;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeMultipart;
import javax.mail.Multipart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Folder;
import javax.mail.Store;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class EmailUtils {

	/**
	 * Checks credentials {@link address} and {@link password} to be valid.
	 * 
	 * @param address  G-Mail address
	 * @param password supposed password of {@link address}
	 * @return boolean if credentials are valid
	 */
	public static boolean checkCreds(String address, String password) {
		try {
			Properties properties = new Properties();
			properties.put("mail.smtp.host", "smtp.gmail.com");
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(address, password);
				}
			});
			Transport transport = session.getTransport("smtp");
			transport.connect(address, password);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Sends email from {@link address} to {@link recipient} with text {@link body}.
	 * 
	 * @param address   G-Mail address to send email from
	 * @param password  password to {@link address}
	 * @param recipient receiver of email
	 * @param body      body of email
	 * @throws MessagingException
	 */
	public static void sendEmail(String address, String password, String recipient, String body)
			throws MessagingException {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(address, password);
			}
		});
		Message message = new MimeMessage(session);
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setText(body);
		Transport.send(message);
	}

	/**
	 * Receives email from {@link sender} in inbox of G-Mail account with address
	 * {@link address}.
	 * 
	 * @param address  G-Mail address that email to receive was sent to
	 * @param password password to {@link address}
	 * @param sender   sender of email to receive
	 * @return String body of received email
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static String recEmail(String address, String password, String sender)
			throws MessagingException, IOException {
		Properties properties = new Properties();
		properties.setProperty("mail.host", "imap.gmail.com");
		properties.setProperty("mail.port", "995");
		properties.setProperty("mail.transport.protocol", "imaps");
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(address, password);
			}
		});
		Store store = session.getStore("imaps");
		store.connect();
		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);
		Message messages[] = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
		String ret = null;
		for (int k = messages.length - 11; k < messages.length; k++) {
			Message message = messages[k];
			if (((InternetAddress) message.getFrom()[0]).getAddress().contains(sender)) {
				Object content = message.getContent();
				if (content instanceof String)
					ret = (String) content;
				else if (content instanceof Multipart) {
					MimeMultipart mimeMultiPart = (MimeMultipart) content;
					String body = "";
					for (int i = 0; i < mimeMultiPart.getCount(); i++)
						body += (String) mimeMultiPart.getBodyPart(i).getContent();
					ret = body;
				}
				message.setFlag(Flag.DELETED, true);
				break;
			}
		}
		inbox.close(true);
		store.close();
		return ret;
	}

	/**
	 * Gets email address that forwards to phone number {@link number}.
	 * 
	 * @param number phone number to get forwarding email of
	 * @return String email that forwards to {@link number}
	 */
	public static String getEmail(String number) {
		try {
			Document document = Jsoup.connect("http://www.fonefinder.net/findome.php?npa=" + number.substring(0, 3)
					+ "&nxx=" + number.substring(3, 6) + "&thoublock=" + number.substring(6)).get();
			String carrierInfo = document.select(
					"body > center:nth-child(1) > table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(5) > a")
					.toString();
			String carrier = carrierInfo.substring(31, carrierInfo.indexOf(".php"));
			if (carrier.equals("att"))
				return number + "@txt.att.net";
			else if (carrier.equals("tmobile"))
				return number + "@tmomail.net";
			else if (carrier.equals("verizon"))
				return number + "@vtext.com";
			else if (carrier.equals("sprint"))
				return number + "@messaging.sprintpcs.com";
			else if (carrier.equals("metropcs"))
				return number + "@mymetropcs.com";
			else if (carrier.equals("boostmobile"))
				return number + "@sms.myboostmobile.com";
			else if (carrier.equals("cricket"))
				return number + "@sms.cricketwireless.net";
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}
}
package com.github.thatcherdev.climessage;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CLImessage {

	private static Scanner input = new Scanner(System.in);

	/**
	 * Starts CLImessage.
	 * <p>
	 * If directory "userdata" exists, uses {@link #getInput(String)} to prompt user
	 * to select or create conversation and opens or creates conversation.
	 * Otherwise, creates directory "userdata", prompts user for G-Mail address and
	 * password, saves these credentials to "userdata/creds.properties", and runs
	 * {@link #main(String[])}.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		System.out.print(Ansi.ansi().eraseScreen().cursor(0, 0));
		System.out.println("   .aMMMb  dMP     dMP      dMMMMMMMMb  dMMMMMP .dMMMb  .dMMMb  .aMMMb  .aMMMMP dMMMMMP\n"
				+ "  dMP\"VMP dMP     amr      dMP\"dMP\"dMP dMP     dMP\" VP dMP\" VP dMP\"dMP dMP\"    dMP\n"
				+ " dMP     dMP     dMP      dMP dMP dMP dMMMP    VMMMb   VMMMb  dMMMMMP dMP MMP\"dMMMP\n"
				+ "dMP.aMP dMP     dMP      dMP dMP dMP dMP     dP .dMP dP .dMP dMP dMP dMP.dMP dMP\n"
				+ "VMMMP\" dMMMMMP dMP      dMP dMP dMP dMMMMMP  VMMMP\"  VMMMP\" dMP dMP  VMMMP\" dMMMMMP\n");
		System.out.println("Welcome to ClImessage");
		System.out.println("A command line interface SMS text messaging program.\n");
		try {
			if (new File("userdata").isDirectory()) {
				File[] conversations = new File("userdata").listFiles((dir, name) -> name.endsWith(".convo"));
				System.out.println("Select:\n");
				System.out.println("[+] New conversation");
				String consecutiveConvoNumbers = "";
				for (int k = 0; k < conversations.length; k++) {
					String filename = conversations[k].getName();
					System.out.println("[" + (k + 1) + "] " + filename.substring(0, filename.lastIndexOf("-")));
					consecutiveConvoNumbers += Integer.toString(k + 1);
				}
				String choice = getInput("op+" + consecutiveConvoNumbers);
				if (choice.equals("+"))
					newConvo();
				else
					openConvo("userdata" + File.separator + conversations[Integer.parseInt(choice) - 1].getName());
			} else {
				new File("userdata").mkdir();
				System.out.println("This is the initial setup and only has to be done once.\n");
				System.out.println("CLImessage uses your G-Mail account to send and receive SMS text messages.");
				System.out.println(
						"In order for this program to work correctly, 3rd party G-Mail access for your account must be enabled at\nhttps://myaccount.google.com/lesssecureapps\n");
				System.out.println("Enable this and press ENTER to continue...");
				input.nextLine();
				System.out.println("Enter G-Mail address to be used:");
				String address = getInput("address");
				System.out.println("Enter password for '" + address + "'");
				String password = getInput("password:" + address);
				PrintWriter out = new PrintWriter(new File("userdata" + File.separator + "creds.properties"));
				out.println("address=" + address);
				out.println("password=" + password);
				out.flush();
				out.close();
				System.out.println("Setup complete.\nPress ENTER to restart...");
				input.nextLine();
				main(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new conversation.
	 * <p>
	 * Uses {@link #getInput(String)} to get name and phone number for conversation.
	 * Stores these in {@link name} and {@link number} respectively. Creates a new
	 * file with name "{@link name}-{@link number}.convo" to store conversation log.
	 * Starts conversation using {@link #openConvo(String)}.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void newConvo() throws IOException, InterruptedException {
		System.out.println("Enter name of conversation:");
		String name = getInput("");
		System.out.println("Enter phone number:");
		String number = getInput("number");
		String filename = "userdata" + File.separator + name + "-" + number + ".convo";
		new File(filename).createNewFile();
		openConvo(filename);
	}

	/**
	 * Opens conversation.
	 * <p>
	 * Opens conversation with name {@link filename} by creating a new
	 * {@link com.github.thatcherdev.climessage.Messenger} with {@link filename}.
	 * Runs {@link Messenger#start()} to open conversation.
	 * 
	 * @param filename name of '.convo' file to open
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void openConvo(String filename) throws IOException, InterruptedException {
		Messenger messenger = new Messenger(filename);
		messenger.start();
	}

	/**
	 * Gets user input and verify it's validity with {@link type}.
	 * 
	 * @param type type of input
	 * @return user input
	 */
	private static String getInput(String type) {
		System.out.print(">");
		String ret = input.nextLine();
		if (ret.isEmpty())
			return getInput(type);
		else if (type.startsWith("op") && (!type.substring(2).contains(ret) || !(ret.length() == 1)))
			return getInput(type);
		else if (type.equals("address") && !ret.endsWith("@gmail.com")) {
			System.out.println("\nInvalid G-Mail address\nEnter a valid G-Mail address:");
			return getInput(type);
		} else if (type.startsWith("password") && !EmailUtils.checkCreds(type.substring(type.indexOf(":") + 1), ret)) {
			System.out.println(
					"\nEither G-Mail address and password don't match, or 3rd party access is disabled\nEnable 3rd party access and reenter password:");
			return getInput(type);
		} else if (type.equals("number") && EmailUtils.getEmail(ret) == null) {
			System.out.println(
					"\nCould not get carrier of phone number\nSee README for compatible carriers and enter a valid phone number:");
			return getInput(type);
		} else
			System.out.println();
		return ret;
	}
}
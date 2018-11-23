/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 */
package net.tachtler.jilter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

/*******************************************************************************
 * Command Line Interface Argument Parser for Jilter.
 * 
 * @author Klaus Tachtler. <klaus@tachtler.net>
 * 
 *         Homepage : http://www.tachtler.net
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License..
 * 
 *         Copyright (c) 2018 by Klaus Tachtler.
 ******************************************************************************/
public class InfoMilterCLIArgsParser {

	private static Logger log = LogManager.getLogger();

	/**
	 * Constructor.
	 */
	public InfoMilterCLIArgsParser() {
		super();
	}

	protected static InfoMilterCLIArgsParserBean readArgs(InfoMilterCLIArgsParserBean argsBean, String[] args)
			throws ParseException, InfoMilterCLIArgParserException {

		log.debug("*args                                   : " + args);

		final String USAGE = "[-i <IPv4-Address to listen>] [-p <Port to listen>] [-l <Log-Level>] [-h] [-v] [-d]";
		final String HEADER = "\r\nInfoMilter for Sendmail or Postfix to log all possible parts of the e-Mail communication.\r\n\r\n";
		final String FOOTER = "\r\nCopyright (c) 2018 Klaus Tachtler, <klaus@tachtler.net>. All Rights Reserved. Version 1.0.\r\n\r\n";

		Options options = new Options();

		options.addOption("h", "help", false, "Print this usage information");
		options.addOption("v", "version", false, "Version of the program");
		options.addOption("d", "debug", false, "DEBUG mode with runtime output");
		options.addOption("i", "listener", true, "[REQUIRED] IPv4-Address where the milter is listening on");
		options.addOption("p", "port", true, "[REQUIRED] Port where the milter is listening on");
		options.addOption("l", "logging", true,
				"Enables the Logging for Jilter with the specified log level (ALL, INFO, WARN, ERROR, TRACE, DEBUG or OFF)");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args, false);

		/* -h,--help */
		if (cmd.hasOption("h")) {
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.setWidth(80);
			helpFormatter.printHelp(USAGE, HEADER, options, FOOTER);
			System.exit(0);
		}

		/* -v,--version */
		if (cmd.hasOption("v")) {
			System.out.println(FOOTER);
			System.exit(0);
		}

		/* -d,--debug */
		if (cmd.hasOption("d")) {
			Configurator.setRootLevel(Level.DEBUG);
		} else {
			Configurator.setRootLevel(Level.INFO);
		}

		/* -i,--listener <IPv4-Address or Hostname> */
		if (cmd.hasOption("i")) {

			log.debug("*cmd.getOptionValue(\"i\")              : " + cmd.getOptionValue("i"));

			try {
				argsBean.setInetAddress(InetAddress.getByName(cmd.getOptionValue("i")));
			} catch (UnknownHostException eInetAddressHostname) {
				log.error("UnknownHostException                    : " + eInetAddressHostname);
				throw new InfoMilterCLIArgParserException(
						"***** Program stop, because InfoMilter could not be initialized! ***** (For more details, see error messages and caused by below).",
						eInetAddressHostname);
			}

		} else {
			new HelpFormatter().printHelp(USAGE, HEADER, options, FOOTER);
			throw new InfoMilterCLIArgParserException(
					"Required parameter -i,--listener <IPv4-Address or Hostname> NOT specified!");
		}

		/* -p,--port <Port to listen> */
		if (cmd.hasOption("p")) {

			log.debug("*cmd.getOptionValue(\"p\")              : " + cmd.getOptionValue("p"));

			int port = 0;

			try {
				port = Integer.parseInt(cmd.getOptionValue("p"));
			} catch (NumberFormatException eParseInt) {
				log.error("NumberFormatException                   : " + eParseInt);
				throw new InfoMilterCLIArgParserException(
						"***** Program stop, because InfoMilter could not be initialized! ***** (For more details, see error messages and caused by below).",
						eParseInt);
			}

			if (port >= 1 && port <= 65535) {
				argsBean.setPort(port);
			} else {
				throw new InfoMilterCLIArgParserException(
						"Parameter -p,--port <Port to listen> was NOT a valid port number, between 1 and 65535!");
			}

		} else {
			new HelpFormatter().printHelp(USAGE, HEADER, options, FOOTER);
			throw new InfoMilterCLIArgParserException("Required parameter -p,--port <Port to listen> NOT specified!");
		}

		/* -l,--logging <IPv4-Address or Hostname> */
		if (cmd.hasOption("l")) {

			log.debug("*cmd.getOptionValue(\"l\")              : " + cmd.getOptionValue("l"));

			if (cmd.getOptionValue("l").equals("ALL") || cmd.getOptionValue("l").equals("INFO")
					|| cmd.getOptionValue("l").equals("WARN") || cmd.getOptionValue("l").equals("ERROR")
					|| cmd.getOptionValue("l").equals("TRACE") || cmd.getOptionValue("l").equals("DEBUG")
					|| cmd.getOptionValue("l").equals("OFF")) {
				argsBean.setLoggingEnabled(true);
				argsBean.setLogLevel(cmd.getOptionValue("l"));
			} else {
				new HelpFormatter().printHelp(USAGE, HEADER, options, FOOTER);
				throw new InfoMilterCLIArgParserException(
						"Parameter -l,--logging <TCP-Logging Log-Level> is NOT a valid! (Possible values: ALL, INFO, WARN, ERROR, TRACE, DEBUG or OFF)");
			}

		} else {
			argsBean.setLoggingEnabled(false);
			argsBean.setLogLevel("INFO");
		}

		return argsBean;
	}

}

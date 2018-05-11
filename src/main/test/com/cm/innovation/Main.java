package com.cm.innovation;

import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class Main {

	public static void main(String[] args) {
		String s = "coucou";
		StandardPasswordEncoder encoder = new StandardPasswordEncoder();

		String ep = encoder.encode(s);

		System.out.println("Clear–>"+ s);

		System.out.println("Encrypted–>" + ep);

	}

}

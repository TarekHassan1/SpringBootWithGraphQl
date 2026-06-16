package com.graphQLearning.graphQLearningSpring.security;


import java.io.FileWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class JwtKeyGenerator {
    public static void main(String[] args) throws Exception {
        // Generate a 2048-bit RSA key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        Base64.Encoder encoder = Base64.getMimeEncoder(64, new byte[]{'\n'});

        // 1. Write the Private Key (.pem format)
        try (FileWriter privateFile = new FileWriter("src/main/resources/certs/app-private.pem")) {
            privateFile.write("-----BEGIN PRIVATE KEY-----\n");
            privateFile.write(encoder.encodeToString(keyPair.getPrivate().getEncoded()));
            privateFile.write("\n-----END PRIVATE KEY-----\n");
        }

        // 2. Write the Public Key (.pem format)
        try (FileWriter publicFile = new FileWriter("src/main/resources/certs/app-public.pem")) {
            publicFile.write("-----BEGIN PUBLIC KEY-----\n");
            publicFile.write(encoder.encodeToString(keyPair.getPublic().getEncoded()));
            publicFile.write("\n-----END PUBLIC KEY-----\n");
        }

       System.out.println("Keys generated successfully inside src/main/resources/certs/!");
    }
}
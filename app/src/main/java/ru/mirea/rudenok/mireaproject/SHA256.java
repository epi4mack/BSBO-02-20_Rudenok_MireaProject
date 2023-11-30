package ru.mirea.rudenok.mireaproject;

public class SHA256 {

    public static String encrypt_sha256(String input) {
        int[] h = {
                0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
                0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
        };

        int[] k = {
                0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
                0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
                0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
                0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
                0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
                0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
                0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
                0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
                0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
                0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
                0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
                0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
                0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
                0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
                0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
                0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
        };

        byte[] bytes = input.getBytes();
        byte[] padded = padding(bytes);

        for (int i = 0; i < padded.length; i += 64) {
            int[] w = new int[64];

            for (int j = 0; j < 16; j++) {
                w[j] = ((padded[i + (j * 4)] & 0xFF) << 24) |
                        ((padded[i + (j * 4) + 1] & 0xFF) << 16) |
                        ((padded[i + (j * 4) + 2] & 0xFF) << 8) |
                        (padded[i + (j * 4) + 3] & 0xFF);
            }

            for (int j = 16; j < 64; j++) {
                int s0 = rightRotate(w[j - 15], 7) ^ rightRotate(w[j - 15], 18) ^ (w[j - 15] >>> 3);
                int s1 = rightRotate(w[j - 2], 17) ^ rightRotate(w[j - 2], 19) ^ (w[j - 2] >>> 10);
                w[j] = w[j - 16] + s0 + w[j - 7] + s1;
            }

            int[] temp = new int[8];
            System.arraycopy(h, 0, temp, 0, h.length);

            for (int j = 0; j < 64; j++) {
                int s0 = rightRotate(temp[0], 2) ^ rightRotate(temp[0], 13) ^ rightRotate(temp[0], 22);
                int maj = (temp[0] & temp[1]) ^ (temp[0] & temp[2]) ^ (temp[1] & temp[2]);
                int t2 = s0 + maj;
                int s1 = rightRotate(temp[4], 6) ^ rightRotate(temp[4], 11) ^ rightRotate(temp[4], 25);
                int ch = (temp[4] & temp[5]) ^ (~temp[4] & temp[6]);
                int t1 = temp[7] + s1 + ch + k[j] + w[j];

                temp[7] = temp[6];
                temp[6] = temp[5];
                temp[5] = temp[4];
                temp[4] = temp[3] + t1;
                temp[3] = temp[2];
                temp[2] = temp[1];
                temp[1] = temp[0];
                temp[0] = t1 + t2;
            }

            for (int j = 0; j < 8; j++) {
                h[j] += temp[j];
            }
        }

        StringBuilder hash = new StringBuilder();
        for (int value : h) {
            hash.append(String.format("%08x", value));
        }

        return hash.toString();
    }

    private static byte[] padding(byte[] input) {
        int padLength = 64 - (input.length % 64);

        byte[] padded = new byte[input.length + padLength];
        System.arraycopy(input, 0, padded, 0, input.length);
        padded[input.length] = (byte) 0x80;

        long messageLength = input.length * 8;
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) ((messageLength >>> (8 * (7 - i))) & 0xFF);
        }

        return padded;
    }

    private static int rightRotate(int value, int distance) {
        return (value >>> distance) | (value << (32 - distance));
    }
}

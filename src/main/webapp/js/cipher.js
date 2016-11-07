function deriveKey(password, salt, iterations, keyLength, algorithm) {
    return forge.pkcs5.pbkdf2(password, salt, iterations, keyLength/8, algorithm);
}

function encode(text, key, iv, algorithm) {
    var cipher = forge.cipher.createCipher(algorithm, key);
    cipher.start({iv: iv});
    cipher.update(forge.util.createBuffer(text));
    cipher.finish();
    var encrypted = cipher.output;
    return encrypted.toHex();
}

function decode(encodedHex, key, iv, algorithm) {
    var decipher = forge.cipher.createDecipher(algorithm, key);
    decipher.start({iv: iv});
    var e = forge.util.createBuffer(forge.util.hexToBytes(encodedHex));
    decipher.update(e);
    decipher.finish();
    return decipher.output.data;
}

function messageDigest(algorithm, text) {
    var md = null;
    if (algorithm == "md5") {
        md = forge.md.md5.create();
    }
    if (algorithm == "sha512") {
        md = forge.md.sha512.create();
    }
    md.update(text);
    return md.digest().toHex();
}

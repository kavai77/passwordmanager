var iv = "";

function encode(text, password, salt) {
    var key = forge.pkcs5.pbkdf2(password, salt, 16, 32);
    var cipher = forge.cipher.createCipher('AES-CBC', key);
    cipher.start({iv: iv});
    cipher.update(forge.util.createBuffer(text));
    cipher.finish();
    var encrypted = cipher.output;
    return encrypted.toHex();
}

function decode(encodedHex, password, salt) {
    var key = forge.pkcs5.pbkdf2(password, salt, 16, 32);
    var decipher = forge.cipher.createDecipher('AES-CBC', key);
    decipher.start({iv: iv});
    var e = forge.util.createBuffer(forge.util.hexToBytes(encodedHex));
    decipher.update(e);
    decipher.finish();
    return decipher.output.data;
}

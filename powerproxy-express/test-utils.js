const jwt = require("jsonwebtoken");

function nowInSeconds() {
    return Math.floor(Date.now() / 1000);
}

module.exports = {
    validToken: (secret) => {
        const now = nowInSeconds();
        return jwt.sign(
            {
                sub: '123', // user id
                _name: 'User Name', // user name
                _email: 'user@domail.tld', // user email
                _via: 'unittest', // user IdP
                iat: now - 30, // issued 30 seconds ago
                exp: now + 30 // expires 30 seconds in the future
            },
            secret
        );
    },
    expiredToken: (secret) => {
        const now = nowInSeconds();
        return jwt.sign(
            {
                iat: now - 30, // issued 30 seconds ago
                exp: now - 30 // expired 30 seconds ago
                // the other fields are not necessary
            },
            secret
        );
    }
};

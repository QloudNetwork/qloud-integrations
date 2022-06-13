const {expressjwt: jwt} = require("express-jwt");
const cookie = require("cookie");

function powerproxy({secret, credentialsRequired = false}) {
    return function (req, res, next) {
        const cookies = req.headers.cookie ? cookie.parse(req.headers.cookie) : {};
        jwt({
            secret,
            algorithms: ["HS256"],
            getToken: () => cookies["__pp__token__"],
            credentialsRequired,
        })(req, res, (error) => {
            if (error?.name === "UnauthorizedError") {
                res.redirect(303, "/.pp/login");
            } else {
                next(error);
            }
        });
    }
}

module.exports = powerproxy;

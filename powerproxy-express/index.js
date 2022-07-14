const {expressjwt: jwt} = require("express-jwt");
const cookie = require("cookie");

function powerproxy({secret, credentialsRequired = false}) {
    return function (req, res, next) {
        const cookies = req.headers.cookie ? cookie.parse(req.headers.cookie) : {};
        jwt({
            secret,
            algorithms: ["HS256"],
            getToken: () => cookies["__q__token__"],
            credentialsRequired,
        })(req, res, (error) => {
            if (error?.name === "UnauthorizedError") {
                res.redirect(303, "/.q/login");
            } else {
                next(error);
            }
        });
    }
}

module.exports = powerproxy;

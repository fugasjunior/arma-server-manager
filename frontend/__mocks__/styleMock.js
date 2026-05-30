module.exports = new Proxy({}, {get: (_, key) => key});

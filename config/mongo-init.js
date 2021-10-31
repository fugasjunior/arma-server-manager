db.createUser(
        {
            user: "arma3gui_user",
            pwd: "test",
            roles: [
                {
                    role: "readWrite",
                    db: "arma3gui"
                }
            ]
        }
);
import React from "react";
import {Link, NavLink} from "react-router-dom";

const Navbar = () => {
    return (
        <nav className="navbar navbar-expand-md navbar-dark bg-dark mb-4">
            <Link className="navbar-brand" to="/">
                Arma 3 Server GUI
            </Link>
            <button className="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse"
                    aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
                <span className="navbar-toggler-icon"/>
            </button>
            <div className="collapse navbar-collapse" id="navbarCollapse">
                <ul className="navbar-nav mr-auto">
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/dashboard">Dashboard</NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/settings">Settings</NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/mods">Mods</NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/scenarios">Scenarios</NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/config">App config</NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/additionalServers">Additional servers</NavLink>
                    </li>
                </ul>
            </div>
        </nav>
    );
};

export default Navbar;
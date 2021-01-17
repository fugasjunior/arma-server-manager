import React, {Component} from 'react';
import {isAuthenticated, login} from "../services/authService";
import {Redirect} from "react-router-dom";

class Login extends Component {

    state = {
        username: "",
        password: "",
        error: false
    }

    handleSubmit = async e => {
        e.preventDefault();
        await this.doSubmit();
    };

    doSubmit = async () => {
        try {
            const {username, password} = this.state;
            await login(username, password);
            window.location = "/";
        } catch (e) {
            if (e.response && e.response.status === 401) {
                this.setState({error: true});
            }
        }
    };

    handleUsernameChange = ({currentTarget: input}) => {
        this.setState({username: input.value});
    }

    handlePasswordChange = ({currentTarget: input}) => {
        this.setState({password: input.value});
    }

    render() {
        if (isAuthenticated()) {
            return <Redirect to="/"/>;
        }

        return (
                <div className="col-6">
                    {this.state.error && <div className="alert alert-secondary" role="alert">
                        Login failed!
                    </div>}
                    <form onSubmit={this.handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="username">
                                Username
                            </label>
                            <input type="text" id="username" name="username"
                                   className="form-control" placeholder="Enter username"
                                   required
                                   value={this.state.username}
                                   onChange={this.handleUsernameChange}/>
                        </div>
                        <div className="form-group">
                            <label htmlFor="password">
                                Password
                            </label>
                            <input type="password" id="password" name="password"
                                   className="form-control" placeholder="Password"
                                   required
                                   value={this.state.password}
                                   onChange={this.handlePasswordChange}/>
                        </div>
                        <div>
                            <button type="submit" className="btn btn-primary">Submit</button>
                        </div>
                    </form>
                </div>
        )
    }
}

export default Login;
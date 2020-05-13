import React, {Component} from "react";
import {getAuth, setAuth} from "../services/configService";
import {toast} from "react-toastify";

class AppConfig extends Component {

    state = {
        auth: {
            username: "",
            password: "",
            steamGuardToken: ""
        }
    }

    async componentDidMount() {
        try {
            const {data: auth} = await getAuth();
            this.setState({auth});
        } catch (e) {
            console.error(e);
            toast.error("Could not load auth from server");
        }
    }

    renderInputBox = (name, label, type) => {
        return (
            <div className="form-group">
                <label htmlFor={name}>{label}</label>
                <input className="form-control"
                       type={type ? type : "text"}
                       id={name} name={name}
                       placeholder={label}
                       value={this.state.auth[name]}
                       onChange={this.handleInputChange}
                />
            </div>
        );
    };

    handleInputChange = ({currentTarget: input}) => {
        const currentData = {...this.state.auth}
        currentData[input.name] = input.value;

        this.setState({auth: currentData});
    }

    handleSubmit = async e => {
        e.preventDefault();

        try {
            await setAuth(this.state.auth);
            toast.success("Steam auth successfully set");
        } catch (e) {
            console.error(e);
            toast.error("Setting steam auth failed");
        }
    }

    render() {
        return (
            <div>
                <h2>App configuration</h2>
                <h3>Steam account</h3>
                <p>Steam account with a copy of Arma 3 is needed for downloading workshop mods and keeping
                    them up to date</p>
                <p>If you have Steam Guard 2FA enabled, please fill in the optional token field. You will receive
                    this token in your email upon the first attempt to download any Workshop item</p>
                <form onSubmit={this.handleSubmit}>
                    {this.renderInputBox("username", "Username")}
                    {this.renderInputBox("password", "Password", "password")}
                    {this.renderInputBox("steamGuardToken", "Steam Guard Token")}
                    <button type="submit" className="btn btn-primary">Submit</button>
                </form>
            </div>
        )
    }
}

export default AppConfig;
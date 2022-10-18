import React, {Component} from "react";
import Joi from "joi-browser"
import {getServerSettings, setServerSettings} from "../services/serversService";
import {toast} from "react-toastify";
import TextareaAutosize from "react-textarea-autosize";

class ServerSettingsForm extends Component {

    state = {
        server: {
            name: "",
            port: "",
            maxPlayers: "",
            password: "",
            adminPassword: "",
            adminSteamIds: "",
            messageOfTheDay: "",
            clientFilePatching: false,
            serverFilePatching: false,
            persistent: false,
            battlEye: false,
            von: false,
            verifySignatures: false,
            additionalOptions: "",
        },
        activeMods: [],
        errors: {}
    }

    schema = {
        name: Joi.string().required().error(() => {
            return {
                message: 'Server name is required.',
            };
        }),
        port: Joi.number().integer().min(1).max(65534).required().error(() => {
            return {
                message: 'A number between 1 and 65534 is required',
            };
        }),
        maxPlayers: Joi.number().integer().min(1).required().error(() => {
            return {
                message: 'Max players value is required',
            };
        }),
        password: Joi.string().allow(null).optional(),
        adminPassword: Joi.string().allow(null).optional(),
        clientFilePatching: Joi.boolean(),
        serverFilePatching: Joi.boolean(),
        persistent: Joi.boolean(),
        battlEye: Joi.boolean(),
        von: Joi.boolean(),
        verifySignatures: Joi.boolean(),
    };

    async componentDidMount() {
        let {data: server} = await getServerSettings();
        this.setState({server});
    };

    validate = () => {
        const options = {abortEarly: false, allowUnknown: true}
        const {error} = Joi.validate(this.state.server, this.schema, options);
        if (!error) return null;

        const errors = {};
        for (let item of error.details) errors[item.path[0]] = item.message;
        return errors;
    }

    handleSubmit = async (e) => {
        e.preventDefault();

        const errors = this.validate();
        this.setState({errors: errors || {}});
        if (errors) return;

        try {
            await setServerSettings(this.state.server);
            toast.success("Server settings successfully changed");
        } catch (e) {
            console.error(e);
            toast.error("Submitting server settings failed");
        }
    }

    handleInputChange = ({currentTarget: input}) => {
        const currentData = {...this.state.server}
        currentData[input.name] = input.value;

        this.setState({server: currentData});
    }

    handleCheckboxChange = ({currentTarget: input}) => {
        const currentData = {...this.state.server}
        currentData[input.name] = input.checked;

        this.setState({server: currentData});
    };

    renderInputBox = (name, label) => {
        return (
            <div className="form-group row">
                <label htmlFor={name} className="col-sm-2 col-form-label">{label}</label>
                <div className="col-sm-10">
                    <input className="form-control"
                           id={name} name={name}
                           placeholder={label}
                           value={this.state.server[name]}
                           onChange={this.handleInputChange}
                    />
                    {this.state.errors[name] && <div className="alert alert-danger">{this.state.errors[name]}</div>}
                </div>
            </div>
        );
    };

    renderTextArea = (name, label) => {
        return (
            <div className="form-group row">
                <label htmlFor={name} className="col-sm-2 col-form-label">{label}</label>
                <div className="col-sm-10">
                    <TextareaAutosize className="form-control"
                              id={name} name={name}
                              placeholder={label}
                              value={this.state.server[name]}
                              onChange={this.handleInputChange}
                    />
                    {this.state.errors[name] && <div className="alert alert-danger">{this.state.errors[name]}</div>}
                </div>
            </div>
        );
    };

    renderCheckBox = (name, label) => {
        return (
            <div className="col-sm-3">
                <div className="form-check form-check-inline">
                    <input className="form-check-input" type="checkbox"
                           id={name} name={name}
                           onChange={this.handleCheckboxChange}
                           checked={this.state.server[name]}/>
                    <label className="form-check-label" htmlFor={name}>{label}</label>
                </div>
            </div>
        );
    };

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <h2 className="mb-4">Server settings</h2>
                {this.renderInputBox("name", "Server name")}
                {this.renderInputBox("port", "Port")}
                {this.renderInputBox("maxPlayers", "Max players")}
                {this.renderInputBox("password", "Password")}
                {this.renderInputBox("adminPassword", "Admin password")}

                <div className="form-group row">
                    <div className="col-sm-2"/>
                    {this.renderCheckBox("clientFilePatching", "Client file patching")}
                    {this.renderCheckBox("serverFilePatching", "Server file patching")}
                    {this.renderCheckBox("persistent", "Persistent")}
                </div>
                <div className="form-group row">
                    <div className="col-sm-2"/>
                    {this.renderCheckBox("battlEye", "BattlEye")}
                    {this.renderCheckBox("von", "VON enabled")}
                    {this.renderCheckBox("verifySignatures", "Verify signatures")}
                </div>
                {this.renderTextArea("additionalOptions", "Additional options")}

                <button className="btn btn-primary btn-lg" type="submit">Save</button>
            </form>
        );
    }
}

export default ServerSettingsForm;
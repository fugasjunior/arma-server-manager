import React, {Component} from "react";

class ModInstallForm extends Component {
    state = {
        modId: ""
    }

    handleIdChange = ({currentTarget: input}) => {
        console.log(input);
        let value = input.value.trim().replace(/\D/g, '');
        this.setState({modId: value});
    }

    render() {
        return (
            <React.Fragment>
                <h3>Install new mod</h3>

                <form className="form-inline"
                      onSubmit={(e) => this.props.onSubmit(this.state.modId, e)}>
                    <div className="form-group">
                        <label htmlFor="modId" className="sr-only">Steam Workshop Mod ID</label>
                        <input type="text"
                               className="form-control"
                               id="modId" name="modId"
                               placeholder="Workshop mod ID"
                               value={this.state.modId}
                               onChange={this.handleIdChange}
                        />
                    </div>
                    <button type="submit"
                            className="btn btn-primary ml-2"
                            disabled={this.state.modId.length < 1}>Install
                    </button>
                </form>
            </React.Fragment>
        );
    }
}

export default ModInstallForm;


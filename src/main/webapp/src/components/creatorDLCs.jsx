import React, {Component} from "react";
import {getCreatorDLCs, updateCreatorDLC} from "../services/creatorDLCsService";

class CreatorDLCs extends Component {

    state = {
        dlcs: [],
    };

    async componentDidMount() {
        const {data: data} = await getCreatorDLCs();
        this.setState({dlcs: data.creatorDLCs});
    }

    onEnabledChange = async (id) => {
        const dlcs = [...this.state.dlcs];
        let dlc = dlcs.find(dlc => dlc.id === id);
        dlc.enabled = !dlc.enabled;
        await updateCreatorDLC(dlc);
        this.setState(dlcs);
    }

    render() {
        return (
                <React.Fragment>
                    <div className="row row-cols-1 row-cols-md-2">
                        {this.state.dlcs.map(dlc => (
                                <div className="col mb-4">
                                    <div className="card">
                                        {dlc.imageUrl &&
                                        <img src={dlc.imageUrl} className="card-img-top" alt={dlc.name}
                                             style={{maxHeight: "250px"}}
                                        />}
                                        <div className="card-body">
                                            <h5 className="card-title">{dlc.name}</h5>
                                            {dlc.description && <p>{dlc.description}</p>}
                                            <label>
                                                <input type="checkbox"
                                                       value={dlc.id}
                                                       checked={dlc.enabled}
                                                       onChange={() => this.onEnabledChange(dlc.id)}/>
                                                &nbsp;Active
                                            </label>
                                        </div>
                                    </div>
                                </div>
                        ))}
                    </div>
                </React.Fragment>
        )
    }
}

export default CreatorDLCs;
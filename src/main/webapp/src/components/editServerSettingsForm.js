import {Field, Form, Formik} from "formik";
import {TextareaAutosize} from "@mui/material";
import React from "react";

const EditServerSettingsForm = props => {

    return (
            <Formik initialValues={props.server}
                    onSubmit={props.onSubmit}>
                <Form>
                    <div className="form-group row">
                        <label htmlFor="name" className="col-sm-2 col-form-label">Server name</label>
                        <div className="col-sm-10">
                            <Field id="name" name="name" placeholder="Server name" className="form-control"/>
                        </div>
                    </div>

                    <div className="form-group row">
                        <label htmlFor="port" className="col-sm-2 col-form-label">Port</label>
                        <div className="col-sm-10">
                            <Field id="port" name="port" type="number" placeholder="Port" className="form-control"/>
                        </div>
                    </div>

                    <div className="form-group row">
                        <label htmlFor="queryPort" className="col-sm-2 col-form-label">Query port</label>
                        <div className="col-sm-10">
                            <Field id="queryPort" name="queryPort" type="number" placeholder="Query port"
                                   className="form-control"/>
                        </div>
                    </div>

                    <div className="form-group row">
                        <label htmlFor="maxPlayers" className="col-sm-2 col-form-label">Max players</label>
                        <div className="col-sm-10">
                            <Field id="maxPlayers" name="maxPlayers" type="number" placeholder="32"
                                   className="form-control"/>
                        </div>
                    </div>

                    <div className="form-group row">
                        <label htmlFor="password" className="col-sm-2 col-form-label">Server password</label>
                        <div className="col-sm-10">
                            <Field id="password" name="password" placeholder="Server password"
                                   className="form-control"/>
                        </div>
                    </div>

                    <div className="form-group row">
                        <label htmlFor="adminPassword" className="col-sm-2 col-form-label">Admin password</label>
                        <div className="col-sm-10">
                            <Field id="adminPassword" name="adminPassword" placeholder="Admin password"
                                   className="form-control"/>
                        </div>
                    </div>

                    <div className="form-group row">
                        <div className="col-sm-2"/>
                        <div className="col-sm-3">
                            <div className="form-check form-check-inline">
                                <label className="form-check-label">
                                    <Field type="checkbox" name="clientFilePatching" className="form-check-input"/>
                                    Client file patching
                                </label>
                            </div>
                        </div>

                        <div className="col-sm-3">
                            <div className="form-check form-check-inline">
                                <label className="form-check-label">
                                    <Field type="checkbox" name="serverFilePatching" className="form-check-input"/>
                                    Server file patching
                                </label>
                            </div>
                        </div>

                        <div className="col-sm-3">
                            <div className="form-check form-check-inline">
                                <label className="form-check-label">
                                    <Field type="checkbox" name="persistent" className="form-check-input"/>
                                    Persistent
                                </label>
                            </div>
                        </div>
                    </div>

                    <div className="form-group row">
                        <div className="col-sm-2"/>
                        <div className="col-sm-3">
                            <div className="form-check form-check-inline">
                                <label className="form-check-label">
                                    <Field type="checkbox" name="battlEye" className="form-check-input"/>
                                    BattlEye enabled
                                </label>
                            </div>
                        </div>

                        <div className="col-sm-3">
                            <div className="form-check form-check-inline">
                                <label className="form-check-label">
                                    <Field type="checkbox" name="von" className="form-check-input"/>
                                    VON enabled
                                </label>
                            </div>
                        </div>

                        <div className="col-sm-3">
                            <div className="form-check form-check-inline">
                                <label className="form-check-label">
                                    <Field type="checkbox" name="verifySignatures" className="form-check-input"/>
                                    Verify signatures
                                </label>
                            </div>
                        </div>
                    </div>

                    <div className="form-group row">
                        <label htmlFor="additionalOptions" className="col-sm-2 col-form-label">Additional
                            options</label>
                        <div className="col-sm-10">
                            <Field className="form-control" id="additionalOptions" name="additionalOptions"
                                   as={TextareaAutosize} placeholder="Additional options"/>
                        </div>
                    </div>

                    <button className="btn btn-primary btn-lg" type="submit">Submit</button>
                </Form>
            </Formik>
    );
};

export default EditServerSettingsForm;
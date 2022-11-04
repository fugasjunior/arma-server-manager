import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import {BrowserRouter} from "react-router-dom";
import {AuthContextProvider} from "./store/auth-context";
import {DevSupport} from "@react-buddy/ide-toolbox";
import {ComponentPreviews, useInitial} from "./dev";
import {OSContextProvider} from "./store/os-context";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
        <AuthContextProvider>
            <OSContextProvider>
                <BrowserRouter>
                    <React.StrictMode>
                        <DevSupport ComponentPreviews={ComponentPreviews}
                                    useInitialHook={useInitial}
                        >
                            <App/>
                        </DevSupport>
                    </React.StrictMode>
                </BrowserRouter>
            </OSContextProvider>
        </AuthContextProvider>
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();

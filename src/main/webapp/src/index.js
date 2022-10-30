import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import {BrowserRouter} from "react-router-dom";
import {AuthContextProvider} from "./store/auth-context";
import {DevSupport} from "@react-buddy/ide-toolbox";
import {ComponentPreviews, useInitial} from "./dev";

ReactDOM.render(
        <AuthContextProvider>
            <BrowserRouter>
                <React.StrictMode>
                    <DevSupport ComponentPreviews={ComponentPreviews}
                                useInitialHook={useInitial}
                    >
                        <App/>
                    </DevSupport>
                </React.StrictMode>
            </BrowserRouter>
        </AuthContextProvider>,
        document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();

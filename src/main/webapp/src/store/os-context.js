import {createContext, useEffect, useState} from "react";
import {getServerOS} from "../services/systemService";

export const OSContext = createContext({
    os: '',
});

export const OSContextProvider = (props) => {

    const [os, setOs] = useState("");

    useEffect(() => {
        async function fetchOS() {
            const {data: osTypeDto} = await getServerOS();
            setOs(osTypeDto.osType);
        }

        fetchOS();
    }, [])

    const contextValue = {os};

    return <OSContext.Provider value={contextValue}>{props.children}</OSContext.Provider>;
}
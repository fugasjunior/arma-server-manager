import {createContext, useEffect, useState} from "react";
import {getServerOS} from "../services/systemService";

export const OsContext = createContext({
    os: '',
});

export const OSContextProvider = (props: any) => {

    const [os, setOs] = useState("");

    useEffect(() => {
        async function fetchOS() {
            const {data: osTypeDto} = await getServerOS();
            setOs(osTypeDto.osType);
        }

        fetchOS();
    }, [])

    const contextValue = {os};

    return <OsContext.Provider value={contextValue}>{props.children}</OsContext.Provider>;
}
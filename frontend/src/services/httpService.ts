import axios from "axios";
import {toast} from "material-react-toastify";

axios.interceptors.response.use(undefined, error => {
    const expectedError =
        error.response &&
        error.response.status >= 400 &&
        error.response.status < 500;

    if (!expectedError) {
        console.error(error.response.data.message);
        toast.error("An unexpected error occurred.");
    } else if (error.response.data.message) {
        toast.error(error.response.data.message);
    }

    return Promise.reject(error);
});

function setJwt(jwt: string) {
    axios.defaults.headers.common['Authorization'] = jwt;
}

export default {
    get: axios.get,
    post: axios.post,
    put: axios.put,
    delete: axios.delete,
    patch: axios.patch,
    setJwt
};


import axios from "axios";
import env from "dotenv";
env.config();
const API_URL = process.env.API_URL;
const SHOW_LOGS = process.env.API_LOGS.toLowerCase();

export async function post(path = "", body = null) {
    const url = path.startsWith("/") || path == ""? API_URL + path : `${API_URL}/${path}`;

    try {
        let response = await axios.post(url, body,
        {
            headers: { "Content-type": "application/json; charset=UTF-8" },
        });
        if (SHOW_LOGS == "debug") {
            console.log(`Request to ${url}`);
            console.log(response.data)
        }
        
        return response.data;
    } catch (error){
        if (SHOW_LOGS == "debug" || SHOW_LOGS == "error") {
            console.error(`Error in request at ${url}`);
            console.error(error.response);
        }
    }
}

export async function put(path = "", body = null) {
    const url = path.startsWith("/") || path == ""? API_URL + path : `${API_URL}/${path}`;

    try {
        let response = await axios.put(url, body,
        {
            headers: { "Content-type": "application/json; charset=UTF-8" },
        });
        if (SHOW_LOGS == "debug") {
            console.log(`Request to ${url}`);
            console.log(response.data)
        }

        return response.data;
    } catch (error){
        if (SHOW_LOGS == "debug" || SHOW_LOGS == "error") {
            console.error(`Error at request to ${url}`);
            console.error(error.response);
        }
    }
}

export function filterArgs(argv = undefined) {
    const args = {};
    
    (argv? argv : process.argv).filter(arg => {
        return arg.startsWith("--");
    }).forEach(arg => {
        if (arg.includes("=")) {
            const pair = arg.trim().split("=");
            args[pair[0].slice(2)] = pair[1];
        } else args[arg.slice(2)] = "true"; 
    })
    return args;
}
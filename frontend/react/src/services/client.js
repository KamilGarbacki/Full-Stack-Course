import axios from 'axios';

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem("access_token")}`
    }
})

export const getCustomers = async () =>{
    try{
        return await axios.get(
            `https://${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            getAuthConfig()
        );
    } catch (e){
        throw e;
    }
}

export const saveCustomer = async (customer) => {
    try{
        return await axios.post(
            `https://${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer
        )
    } catch (e) {
        throw e;
    }
}

export const deleteCustomer = async (id) => {
    try{
        return await axios.delete(
            `https://${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`,
            getAuthConfig()
        )

    } catch (e) {
        throw e;
    }
}

export const editCustomer = async (id, newCustomer) => {
    try{
        return await axios.patch(
            `https://${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`,
            newCustomer,
            getAuthConfig()
        )
    } catch (e) {
        throw e;
    }
}

export const login = async (usernameAndPassword) => {
    try {
        return await axios.post(
             `https://${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/login`,
            usernameAndPassword
        )
    } catch (e) {
        throw e;
    }
}
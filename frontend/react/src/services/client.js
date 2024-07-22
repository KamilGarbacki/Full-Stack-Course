import axios from 'axios';

export const getCustomers = async () =>{
    try{
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`);
    } catch (e){
        throw e;
    }
}

export const saveCustomer = async (customer) => {
    try{
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
            customer
        )
    } catch (e) {
        throw e;
    }
}

export const deleteCustomer = async (id) => {
    try{
        return await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`)
    } catch (e) {
        throw e;
    }
}

export const editCustomer = async (id, newCustomer) => {
    try{
        return await axios.patch(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`,
            newCustomer
        )
    } catch (e) {
        throw e;
    }
}
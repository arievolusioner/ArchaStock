import axios from 'axios';

const axiosClient = axios.create({
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
},  (error) => {
    return Promise.reject(error);
});

axiosClient.interceptors.response.use((response) => {
    return response;
}, (error) => {
    const { response } = error;
    if (response && response.status === 401) {
        localStorage.removeItem('token');
        window.location.href = '/';
    }
    return Promise.reject(error);
});

export default axiosClient;
import axios from "axios";
import ConstantList from "../../appConfig";
const API_PATH = ConstantList.API_ENPOINT + "/api/labour-agreement-attachment";

export async function uploadFile(file) {
    const url = API_PATH + "/upload";
    let formData = new FormData();
    formData.append('uploadfile', file);//Lưu ý tên 'uploadfile' phải trùng với tham số bên Server side
    const config = {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }
    return axios.post(url, formData, config);
}

export async function downloadFile(fileId) {
    if (!fileId) return null;
    const url = API_PATH + "/download/" + fileId;

    return axios({
        url: url, // Replace with your API endpoint
        method: 'GET',
        responseType: 'blob', // important
    });
}

export async function previewFile(fileId) {
    if (!fileId) return null;
    const url = `${API_PATH}/download/${fileId}`;

    try {
        const response = await axios({
            url: url,
            method: 'GET',
            responseType: 'blob', // important
        });

        return response.data; // Return the Blob
    } catch (error) {
        console.error("Failed to download file", error);
        throw error;
    }
}
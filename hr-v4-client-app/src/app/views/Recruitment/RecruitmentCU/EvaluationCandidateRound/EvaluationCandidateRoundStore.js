import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import { getByCandidateRoundId, saveEvaluationCandidate } from "./EvaluationCandidateRoundService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class EvaluationCandidateRoundStore {
    selectedEvaluationCandidateRound = null;
    openFormEvaluationCandidateRound = false;
    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.selectedEvaluationCandidateRound = null;
        this.openFormEvaluationCandidateRound = false;
    };
    handleClose = () => {
        this.openFormEvaluationCandidateRound = false;
    };
    handleOpenFormEvaluationCandidateRound = async (candidateRoundId) => {
        if (!candidateRoundId) return;

        try {
            const response = await getByCandidateRoundId(candidateRoundId);
            const { status, message, data } = response?.data;

            if (status === 200) {
                console.log(data);
                this.selectedEvaluationCandidateRound = data    ;
                this.openFormEvaluationCandidateRound = true;
            } else if (status >= 400 && status < 500) {
                toast.warn(message || "Không tìm thấy dữ liệu ứng viên.");
            } else if (status >= 500) {
                toast.error("Lỗi hệ thống, vui lòng thử lại sau.");
            } else {
                toast.info("Phản hồi không xác định từ server.");
            }

            console.log(data);
        } catch (error) {
            console.error("Lỗi khi mở form:", error);
            toast.error("Không thể kết nối tới server.");
        }
    };

    saveEvaluationCandidate = async (values) => {
        try {
            const response = await saveEvaluationCandidate(values);
            const { status, message, data } = response;

            if (status === 200) {
                toast.success(message || "Lưu thành công!");
            } else if (status >= 400 && status < 500) {
                toast.warn(message || "Có lỗi xảy ra khi lưu (Client error).");
            } else if (status >= 500) {
                toast.error("Lỗi hệ thống, vui lòng thử lại sau.");
            } else {
                toast.info("Phản hồi không xác định từ server.");
            }

            console.log(data);
        } catch (error) {
            console.error("Lỗi khi lưu:", error);
            toast.error("Không thể kết nối tới server.");
        }
    };
}

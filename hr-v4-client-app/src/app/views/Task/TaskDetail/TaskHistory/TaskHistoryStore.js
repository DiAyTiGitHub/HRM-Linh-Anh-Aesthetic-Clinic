import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import i18n from "i18n";
import { v4 as uuid } from 'uuid';
import {
    pagingHistoryOfTask,
    getAllHistoryOfTask,
    createHistoryComment
} from './TaskHistoryService';

export default class TaskHistoryStore {
    canLoadMore = true;
    loadedHistory = [];

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.loadedHistory = [];
        this.canLoadMore = true;
    }

    pagingHistoryOfTask = async (searchObj) => {
        try {
            const searchData = {
                taskId: searchObj?.taskId,
                pageIndex: searchObj?.pageIndex,
                pageSize: 5,
            }

            const { data } = await pagingHistoryOfTask(searchObj?.taskId, searchData);
            this.loadedHistory = [...this.loadedHistory, ...data];

            if (!data || data?.length === 0 || 5 > data?.length) {
                this.canLoadMore = false;
            }
        }
        catch (err) {
            console.error(err);

            toast.error("Có lỗi xảy ra khi tải lịch sử, vui lòng thử lại");
        }

    }

    getAllHistoryOfTask = async (taskId) => {
        try {
            const { data } = await getAllHistoryOfTask(taskId);
            this.loadedHistory = data;

            if (!data || data?.length === 0 || 5 > data?.length) {
                this.canLoadMore = false;
            }
        }
        catch (err) {
            console.error(err);

            toast.error("Có lỗi xảy ra khi tải lịch sử, vui lòng thử lại");
        }
    }

    createHistoryComment = async (comment) => {
        try {
            const { data } = await createHistoryComment(comment);
            this.loadedHistory = [data, ...this.loadedHistory];
        }
        catch (err) {
            console.error(err);
            toast.error("Có lỗi xảy ra khi để lại bình luận, vui lòng thử lại sau");
        }
    }

    timestampToDate = (timestamp) => {
        // console.log("timestamp: ", timestamp);
        const date = new Date(timestamp);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }

    convertToTimeToTimestamp = time => {
        // Parse the date string
        let timestamp = Date.parse(time);

        // Check if the parsing was successful
        if (!isNaN(timestamp)) {
            return timestamp; // Return the timestamp
        } else {
            return null; // Return null if parsing failed
        }
    }

    convertToYMDAgo = (inputDate) => {
        const timestamp = this.convertToTimeToTimestamp(inputDate);

        const seconds = Math.floor((new Date() - timestamp) / 1000);
        let interval = Math.floor(seconds / 31536000);

        if (interval >= 1) {
            const remainderMonths = Math.floor((seconds % 31536000) / 2592000);
            if (remainderMonths > 0) {
                const remainderDays = Math.floor((seconds % 2592000) / 86400);
                if (remainderDays > 0) {
                    const remainderHours = Math.floor((seconds % 86400) / 3600);
                    if (remainderHours > 0) {
                        return interval + " năm " + remainderMonths + " tháng " + remainderDays + " ngày " + remainderHours + " giờ trước";
                    } else {
                        return interval + " năm " + remainderMonths + " tháng " + remainderDays + " ngày trước";
                    }
                } else {
                    return interval + " năm " + remainderMonths + " tháng trước";
                }
            } else {
                return "Hơn " + interval + " năm trước";
            }
        }
        interval = Math.floor(seconds / 2592000);
        if (interval >= 1) {
            const remainderDays = Math.floor((seconds % 2592000) / 86400);
            if (remainderDays > 0) {
                const remainderHours = Math.floor((seconds % 86400) / 3600);
                if (remainderHours > 0) {
                    return interval + " tháng " + remainderDays + " ngày " + remainderHours + " giờ trước";
                } else {
                    return interval + " tháng " + remainderDays + " ngày trước";
                }
            } else {
                return "Hơn " + interval + " tháng trước";
            }
        }
        interval = Math.floor(seconds / 86400);
        if (interval >= 1) {
            const remainderHours = Math.floor((seconds % 86400) / 3600);
            if (remainderHours > 0) {
                return interval + " ngày " + remainderHours + " giờ trước";
            } else {
                return interval + " ngày trước";
            }
        }
        interval = Math.floor(seconds / 3600);
        if (interval >= 1) {
            const remainderMinutes = Math.floor((seconds % 3600) / 60);
            if (remainderMinutes > 0) {
                return interval + " giờ " + remainderMinutes + " phút trước";
            } else {
                return interval + " giờ trước";
            }
        }
        interval = Math.floor(seconds / 60);
        if (interval >= 1) {
            return interval + " phút trước";
        }
        return "Vừa xong";
    }

    isObject = (obj) => {
        return typeof obj === 'object' && obj !== null && !(obj instanceof String);
    }

    getKeyTitle = key => {
        if (key == "comment") return "Bình luận";
        if (key == "labels") return "Nhãn công việc";
        if (key == "priority") return "Độ ưu tiên";
        if (key == "orderNumber") return "Thứ tự công việc";
        if (key == "status") return "Trạng thái công việc";
        if (key == "endTime") return "Thời gian kết thúc";
        if (key == "startTime") return "Thời gian bắt đầu";
        if (key == "estimateHour") return "Thời gian ước tính";
        if (key == "code") return "Mã công việc";
        if (key == "description") return "Mô tả công việc";
        if (key == "name") return "Tên công việc";
        if (key == "assignee") return "Người phụ trách";
        if (key == "project") return "Dự án";
        if (key == "activity") return "Hoạt động";

        return null;
    }

    getValueTitleType = value => {
        //if value is just a string (old history), then just display it
        if (!this.isObject(value)) return 0;

        const oldValue = value?.oldValue;
        const newValue = value?.newValue;

        if (oldValue == "") {
            // return " đã được tạo mới (" + newValue + ")";
            return 1;
        }
        if (newValue == "") {
            // return " cũ (" + oldValue + ") đã bị xóa";
            return 2;
        }

        // return " thay đổi từ \"" + oldValue + "\" thành \"" + newValue + "\"";
        return 3;
    }

    isContentEmpty = (content) => {
        if (!content) return true;

        // Remove only certain tags that do not contribute to visible content
        const strippedContent = content.replace(/<[^\/>][^>]*><\/[^>]+>|<br\s*\/?>/g, "").trim();

        // Check if the remaining content is empty
        return strippedContent.length === 0;
    };
}

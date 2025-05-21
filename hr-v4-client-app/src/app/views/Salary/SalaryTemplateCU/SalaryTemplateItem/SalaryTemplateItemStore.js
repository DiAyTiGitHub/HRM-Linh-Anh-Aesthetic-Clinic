import {makeAutoObservable, runInAction} from "mobx";
import {
    saveOrUpdateWithItemConfig,
} from "../SalaryTemplateItemService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";

export default class SalaryTemplateItemStore {
    selectedSalaryTemplateItem = null;
    shouldOpenEditorDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    handleClose = () => {
        this.selectedSalaryTemplateItem = null;
        this.shouldOpenEditorDialog = false;
    };

    handleSelectedSalaryTemplateItem = (templateItem) => {
        this.selectedSalaryTemplateItem = templateItem;
        this.shouldOpenEditorDialog = true;
        console.log(this.selectedSalaryTemplateItem);
    };

    saveOrUpdateWithItemConfig = async (templateItem) => {
        try {
            const res = await saveOrUpdateWithItemConfig(templateItem);
            toast.success(
                templateItem?.id ? "Chỉnh sửa thành công!" : "Thêm mới thành công!"
            );
            this.handleClose();
            return res?.data;
        } catch (error) {
            console.log(error);
            toast.warning("Có lỗi xảy ra!");
        }
    };
}

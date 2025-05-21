import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import LocalConstants from "../../../LocalConstants";
import GlobitsNumberInput from "../../../common/form/GlobitsNumberInput";

function TableStaffSalaryItemValueList() {

    const {staffSalaryItemValueStore} = useStore();
    const {t} = useTranslation();
    const {values, setFieldValue} = useFormikContext();
    const [data, setData] = useState([]);

    const {getListSalaryTemplateItem} = staffSalaryItemValueStore;
    const fetchData = async () => {
        if (values?.salaryTemplate && values?.staff) {
            try {
                const response = await getListSalaryTemplateItem({
                    salaryTemplate: values.salaryTemplate,
                    staff: values.staff,
                    getAll: true,
                });
                if (response) {
                    setData(response);
                    setFieldValue("salaryTemplate.templateItems", response);
                }
            } catch (error) {
                console.error("Lỗi khi lấy dữ liệu:", error);
                setData([]);
            }
        } else {
            setData([]);
        }
    };
    useEffect(() => {
        fetchData();
    }, [values?.salaryTemplate?.id, values?.staff?.id]);


    const {
        totalPages, totalElements, handleChangePage, setPageSize, pageIndex, pageSize
    } = staffSalaryItemValueStore;

    // 🛠 Di chuyển columns vào trong component
    const columns = [{
        title: "Tên phần tử lương", field: "salaryItem.name", align: "left",
    }, {
        title: "Giá trị", field: "value", align: "left", render: (rowData) => {
            const currentValue = data?.find(item => item.id === rowData.id)?.value || "";
            return (
                <GlobitsNumberInput
                    name={`value_${rowData.id}`}
                    value={currentValue}
                    onChange={(e) => {
                        const newValue = e.target.value;
                        setFieldValue("salaryTemplate.templateItems",
                            values.salaryTemplate.templateItems.map(item =>
                                item.id === rowData.id ? {...item, value: newValue} : item
                            )
                        );
                    }}
                    disabled={LocalConstants.SalaryItemCalculationType.FIX.value !== rowData?.salaryItem?.calculationType}

                />);


        },
    }, {
        title: "Cách tính", field: "calculationType", align: "left", render: rowData => {
            if (rowData?.salaryItem?.calculationType) return LocalConstants.SalaryItemCalculationType.getListData().find(i => i.value == rowData?.salaryItem?.calculationType)?.name;
            return "";
        },
    },];

    return (<GlobitsTable
        data={data}
        columns={columns}
        totalPages={totalPages}
        handleChangePage={handleChangePage}
        setRowsPerPage={setPageSize}
        pageSize={pageSize}
        pageSizeOption={[10, 15, 25, 50, 100]}
        totalElements={totalElements}
        page={pageIndex}
        nonePagination={true}
    />);
}

export default memo(observer(TableStaffSalaryItemValueList));

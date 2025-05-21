import React, { useState, memo } from "react";
import { observer } from "mobx-react";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import SalaryBoardViewRow from "./SalaryBoardViewRow";
import SalaryBoardEditableRow from "./SalaryBoardEditableRow";
import { useFormikContext } from "formik";
import { useStore } from "app/stores";

function SalaryBoardRowContainer(props) {
    const {
        rowIndex,
        data,
    } = props;

    const [isOnUpdating, setIsOnUpdating] = useState(false);
    const [intactUpdateRow, setIntactUpdateRow] = useState(null);
    const { values, setFieldValue } = useFormikContext();

    const {
        salaryResultDetailStore
    } = useStore();

    const {
        saveSalaryResultStaff,
        handleOpenConfirmDeleteResultStaff,
        setSelectedResultStaff
    } = salaryResultDetailStore;

    function handleOnUpdateRow() {
        const needUpdateData = values?.salaryResultStaffs[rowIndex];
        setIntactUpdateRow(JSON.parse(JSON.stringify(needUpdateData)));

        setIsOnUpdating(true);
    }

    function handleCancelOnUpdateRow() {
        if (intactUpdateRow) {
            setFieldValue(`salaryResultStaffs[${rowIndex}]`, intactUpdateRow);
        }

        setIsOnUpdating(false);
    }

    async function handleSaveOnUpdateRow() {
        try {
            const needUpdateData = values?.salaryResultStaffs[rowIndex];

            const updatedData = await saveSalaryResultStaff(needUpdateData);
            setFieldValue(`salaryResultStaffs[${rowIndex}]`, updatedData);

            setIsOnUpdating(false);
        }
        catch (error) {
            console.error(error);
        }
    }

    function handleDeleteResultStaff() {
        // Find the selected staff based on data?.staffId
        const selectedStaff = (values?.staffs || []).find(
            (staff) => staff.id === data?.staffId
        );

        // If found, create a deep copy of the selected staff
        if (!selectedStaff) {
            console.log("No matching staff found.");
            return;
        }

        const onDeleteRow = {
            ...data,
            selectedStaff
        }

        setSelectedResultStaff(JSON.parse(JSON.stringify(onDeleteRow)));
        handleOpenConfirmDeleteResultStaff();
    }

    return (
        <tr className='row-table-body row-table-no_data'>
            <td className="stickyCell">
                <div className="flex flex-middle justify-center">

                    {!isOnUpdating && (
                        <Tooltip title="Cập nhật" placement="top">
                            <IconButton className="ml-8 bg-white" size="small" onClick={handleOnUpdateRow}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}

                    {
                        isOnUpdating && (
                            <Tooltip title="Lưu kết quả" placement="top">
                                <IconButton className="ml-8 bg-white" size="small" onClick={handleSaveOnUpdateRow}>
                                    <Icon fontSize="small" style={{ color: "green" }}>
                                        save
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )
                    }

                    {
                        isOnUpdating && (
                            <Tooltip title="Hủy cập nhật" placement="top">
                                <IconButton className="ml-8 bg-white" size="small" onClick={handleCancelOnUpdateRow}>
                                    <Icon fontSize="small" style={{ color: "red" }}>
                                        clear
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )
                    }

                    <Tooltip title="Xóa nhân viên khỏi bảng lương" placement="top">
                        <IconButton className="ml-8 bg-white" size="small"
                            onClick={handleDeleteResultStaff}
                        >
                            <Icon fontSize="small" color="secondary">
                                delete
                            </Icon>
                        </IconButton>
                    </Tooltip>
                </div >
            </td>


            {!isOnUpdating && (
                <SalaryBoardViewRow
                    data={data}
                />
            )}

            {isOnUpdating && (
                <SalaryBoardEditableRow
                    data={data}
                    rowIndex={rowIndex}
                />
            )}
        </tr>
    );
}

export default memo(observer(SalaryBoardRowContainer));
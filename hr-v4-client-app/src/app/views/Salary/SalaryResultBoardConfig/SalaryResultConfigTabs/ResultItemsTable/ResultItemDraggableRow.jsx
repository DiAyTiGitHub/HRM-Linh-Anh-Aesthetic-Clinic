import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useSortable } from "@dnd-kit/sortable";
import { Tooltip } from "@material-ui/core";
import DragIndicatorIcon from '@material-ui/icons/DragIndicator';
import { useFormikContext } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import EditIcon from '@material-ui/icons/Edit';
import { Delete } from "@material-ui/icons";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import LocalConstants from "app/LocalConstants";
import { CSS } from "@dnd-kit/utilities";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";

function ResultItemDraggableRow(props) {
    const { t } = useTranslation();

    const {
        index,
        remove,
        nameSpace,
        disabled,
        data,
        // handleOpenCUResultItem
    } = props;

    const {
        attributes,
        listeners,
        transform,
        transition,
        setNodeRef,
        isDragging
    } = useSortable({
        id: data?.id
    });

    const style = {
        transform: CSS.Transform.toString(transform),
        transition: transition
    };

    const { values, setFieldValue } = useFormikContext();

    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`
        return nameSpace;
    }

    const [openConfirmDeletePopup, setOpenConfirmDeletePopup] = useState(false);

    function handleConfirmDeleteItem() {
        // setOpenConfirmDeletePopup(true);
        remove();
    }

    // function handleEditItem() {
    //     handleOpenCUResultItem(data);
    // }

    return (
        <>
            <tr
                className='row-table-body bg-white'
                key={index}
                ref={setNodeRef}
                style={style}
            >
                {isDragging && (
                    <td colSpan={8} style={{ height: "40px" }}></td>)
                }

                {!isDragging && (
                    <>
                        <td align='center' className="stickyCell">
                            <div className='w-100 one_row_display_cell aligns-center justify-center px-6'>
                                <Tooltip placement="top" title="Sắp xếp thứ tự hiển thị">
                                    <span
                                        {...attributes} {...listeners}
                                        className="pr-8 pointer tooltip"
                                        style={{ cursor: 'grabbing', outline: "none" }}
                                    >
                                        <DragIndicatorIcon className="text-green font-size-20" />
                                    </span>
                                </Tooltip>

                                {/* <Tooltip placement="top" title="Chỉnh sửa chi tiết">
                                    <span
                                        className="pr-8 pointer tooltip"
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => handleEditItem()}
                                    >
                                        <EditIcon className="text-primary font-size-20" />
                                    </span>
                                </Tooltip> */}

                                <Tooltip placement="top" title="Xóa">
                                    <span
                                        className="pointer tooltip"
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => setOpenConfirmDeletePopup(true)}
                                    >
                                        <Delete className="text-red font-size-20" />
                                    </span>
                                </Tooltip>
                            </div>
                        </td>

                        <td className="">
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {data?.salaryItem?.name || ""}
                            </span>
                        </td>

                        <td className="">
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {data?.salaryItem?.code || ""}
                            </span>
                        </td>

                        <td>
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {LocalConstants.SalaryItemType.getListData().find(i => Number(i.value) == Number(data?.salaryItem?.type))?.name || ""}
                            </span>
                        </td>

                        <td>
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {LocalConstants.SalaryItemCalculationType.getListData().find(i => Number(i.value) == Number(data?.salaryItem?.calculationType))?.name || ""}
                            </span>
                        </td>

                        <td>
                            <GlobitsTextField name={withNameSpace("displayName")} />
                        </td>

                        <td>
                            <GlobitsSelectInput
                                name={withNameSpace("resultItemGroupId")}
                                keyValue="id"
                                options={values?.resultItemGroups}
                            />
                        </td>

                        <td>
                            {/* {data?.salaryItem?.calculationType == LocalConstants.SalaryItemCalculationType.USING_FORMULA.value && ( */}
                            
                            {(data?.calculationType == LocalConstants.SalaryItemCalculationType.USING_FORMULA.value
                                || data?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value
                            ) && (
                                    <Tooltip placement="top" title={data?.usingFormula}>
                                        <GlobitsTextField name={withNameSpace("usingFormula")} />
                                    </Tooltip>
                                )}
                        </td>
                    </>
                )}
            </tr >

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={() => setOpenConfirmDeletePopup(false)}
                    onYesClick={handleConfirmDeleteItem}
                    title={t("confirm_dialog.delete.title")}
                    text={"Bạn có chắc muốn xóa thành phần này?"}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )
            }
        </>
    );
} 

export default memo(observer(ResultItemDraggableRow));
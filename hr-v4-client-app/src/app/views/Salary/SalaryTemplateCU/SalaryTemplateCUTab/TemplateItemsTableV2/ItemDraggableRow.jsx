import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useSortable } from "@dnd-kit/sortable";
import { Checkbox, Icon, IconButton, Tooltip } from "@material-ui/core";
import DragIndicatorIcon from '@material-ui/icons/DragIndicator';
import { useFormikContext } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import EditIcon from '@material-ui/icons/Edit';
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import LocalConstants from "app/LocalConstants";
import { CSS } from "@dnd-kit/utilities";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { useStore } from "app/stores";
import GlobitsCheckBox from "../../../../../common/form/GlobitsCheckBox";

function ItemDraggableRow(props) {
    const { t } = useTranslation();
    const {
        index,
        remove,
        nameSpace,
        data,
        handleOpenCUTemplateItem,
        readOnly
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


    const { salaryTemplateItemStore, hrRoleUtilsStore } = useStore();

    const {
        isAdmin,
        isManager,
        isStaffView
    } = hrRoleUtilsStore;

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

    function handleEditItem() {
        handleOpenCUTemplateItem(data);
    }


    const isHiddenOnSalaryBoardChecked = values?.templateItems[index]?.hiddenOnSalaryBoard;
    const isHiddenOnPayslipChecked = values?.templateItems[index]?.hiddenOnPayslip;

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

                        {((isAdmin || isManager) && !readOnly) ? (
                            <td align='center'>
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

                                    <Tooltip placement="top" title="Chỉnh sửa chi tiết">
                                        <span
                                            className="pr-8 pointer tooltip"
                                            style={{ cursor: 'pointer' }}
                                            onClick={() => handleEditItem()}
                                        >
                                            <EditIcon className="text-primary font-size-20" />
                                        </span>
                                    </Tooltip>

                                    {/* <Tooltip placement="top" title="Cập nhật thành phần trong mẫu bảng lương">
                                    <span
                                        className="pointer tooltip pr-8"
                                        style={{ cursor: 'pointer' }}
                                        onClick={() => handleSelectedSalaryTemplateItem(data)}
                                    >
                                        <EditIcon className="text-primary font-size-20" />
                                    </span>
                                    </Tooltip> */}

                                    {(isAdmin || isManager) && (
                                        <Tooltip placement="top" title="Xóa">
                                            <IconButton
                                                size='small'
                                                onClick={() => setOpenConfirmDeletePopup(true)}
                                            >
                                                <Icon fontSize='small' color='error'>
                                                    delete
                                                </Icon>
                                            </IconButton>
                                        </Tooltip>
                                    )}
                                </div>
                            </td>
                        ) : (
                            <td align='center'>{index + 1}</td>
                        )}
                        {/* <td>
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {data?.name || ""}
                            </span>
                        </td> */}

                        <td>
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {data?.code || ""}
                            </span>
                        </td>

                        <td>
                            <>
                                {(data?.calculationType == LocalConstants.SalaryItemCalculationType.USING_FORMULA.value
                                    || data?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value
                                ) && (
                                        <Tooltip placement="top" title={data?.formula}>
                                            <GlobitsTextField
                                                name={withNameSpace("formula")}
                                                multiline
                                                minRows={1}
                                                isTextArea
                                                readOnly={readOnly}
                                            />
                                        </Tooltip>
                                    )}
                            </>

                            {/* {
                                (data?.calculationType === LocalConstants.SalaryItemCalculationType.THRESHOLD.value
                                ) && (
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            label={"Giá trị so sánh ngưỡng"}
                                            name={withNameSpace("salaryItem.formula")}
                                            multiline
                                            rows={2}
                                            disabled={values?.allowanceId}
                                        />
                                    </Grid>
                                )
                            }
                            {
                                (data?.calculationType === LocalConstants.SalaryItemCalculationType.USING_FORMULA.value) && (
                                    <Grid item xs={12}>
                                        <InputFormula
                                            name={withNameSpace("salaryItem.formula")}
                                            valueField={data?.formula || ""}
                                            listData={initialDataInputFormula}
                                            inputRef={inputRefInputFormula}
                                        />
                                    </Grid>
                                )
                            } */}
                        </td>

                        {/* <td>
                            <span
                                className="px-4 one_row_display_cell"
                            >
                                {LocalConstants.SalaryItemType.getListData().find(i => Number(i.value) == Number(data?.type))?.name || ""}
                            </span>
                        </td> */}

                        <td>
                            <span
                                className="one_row_display_cell"
                            >
                                {/* LocalConstants.SalaryItemCalculationType.getListData().find(i => Number(i.value) == Number(data?.calculationType))?.name || ""} */}
                                <GlobitsSelectInput
                                    value={data?.calculationType}
                                    name={withNameSpace("calculationType")}
                                    options={LocalConstants.SalaryItemCalculationType.getListData()}
                                    getOptionDisabled={function (option) {
                                        return option?.value == LocalConstants.SalaryItemCalculationType.FIX.value;
                                    }}
                                    // handleChange={(event) => {
                                    //     setFieldValue(withNameSpace("calculationType"), event.target.value);
                                    // }}
                                    hideNullOption
                                    disabled={data?.calculationType == LocalConstants.SalaryItemCalculationType.FIX.value}
                                    readOnly={readOnly}
                                />

                            </span>
                        </td>

                        <td>
                            <span
                                className="one_row_display_cell"
                            >
                                <GlobitsSelectInput
                                    // label={"Kiểu giá trị"}
                                    name={withNameSpace("valueType")}
                                    options={LocalConstants.SalaryItemValueType.getListData()}
                                    hideNullOption={true}
                                    required
                                    keyValue="value"
                                    readOnly={readOnly}
                                />

                            </span>
                        </td>


                        <td>
                            <GlobitsTextField name={withNameSpace("displayName")} />
                        </td>

                        <td>
                            <GlobitsSelectInput
                                name={withNameSpace("templateItemGroupId")}
                                keyValue="id"
                                options={values?.templateItemGroups}
                                readOnly={readOnly}
                            />
                        </td>

                        <td align="center">
                            <Tooltip title={"Ẩn tại bảng lương"} placement="top">
                                <GlobitsCheckBox
                                    className="pr-16"
                                    id={`radio${data?.id}_hiddenOnSalaryBoard`}
                                    name={withNameSpace("hiddenOnSalaryBoard")}
                                    checked={isHiddenOnSalaryBoardChecked}
                                    onChange={(event) => setFieldValue(withNameSpace("hiddenOnSalaryBoard"), event.target.checked)}
                                    readOnly={readOnly}
                                />
                            </Tooltip>
                        </td>

                        <td align="center">
                            <Tooltip title={"Ẩn tại phiếu lương"} placement="top">
                                <GlobitsCheckBox
                                    className="pr-16"
                                    id={`radio${data?.id}_hiddenOnPayslip`}
                                    name={withNameSpace("hiddenOnPayslip")}
                                    checked={isHiddenOnPayslipChecked}
                                    onChange={(event) => setFieldValue(withNameSpace("hiddenOnPayslip"), event.target.checked)}
                                    readOnly={readOnly}
                                />
                            </Tooltip>
                        </td>


                        {/* <td>
                            {data?.calculationType == LocalConstants.SalaryItemCalculationType.USING_FORMULA.value && (
                                <GlobitsTextField name={withNameSpace("usingFormula")} />
                            )}
                        </td> */}
                    </>
                )}
            </tr>

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
            )}
        </>
    );
}

export default memo(observer(ItemDraggableRow));
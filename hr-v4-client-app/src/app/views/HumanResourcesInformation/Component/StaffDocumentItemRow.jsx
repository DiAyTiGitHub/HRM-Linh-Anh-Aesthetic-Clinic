import { Checkbox } from "@material-ui/core";
import CheckIcon from "@material-ui/icons/Check";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { bytesToKB } from "app/LocalFunction";
import { useStore } from "app/stores";
import SelectFile from "app/views/StaffDocumentItem/SelectFile";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useCallback } from "react";
import "react-toastify/dist/ReactToastify.css";


const StaffDocumentItemRow = memo(({ index, item, nameSpace, isSelected, onSelectRow }) => {
    const withNameSpace = (field) => {
        if (field) return `${nameSpace}.${field}`;
        return nameSpace;
    };

    const { setFieldValue, values } = useFormikContext();
    const { staffDocumentItemStore } = useStore();
    const { setStaffDocumentItemList, staffDocumentItemList } = staffDocumentItemStore;
    const handleAffterSubmitFile = useCallback(
        (file) => {
            // Update the item in staffDocumentItemList
            const updatedList = [...staffDocumentItemList];
            updatedList[index] = {
                ...updatedList[index],
                file,
                isSubmitted: Boolean(file),
                submissionDate: file ? new Date() : null,
            };
            setStaffDocumentItemList(updatedList);

            // Sync with Formik
            setFieldValue(withNameSpace("file"), file);
            setFieldValue(withNameSpace("isSubmitted"), Boolean(file));
            setFieldValue(withNameSpace("submissionDate"), file ? new Date() : null);
        },
        [index, staffDocumentItemList, setStaffDocumentItemList, setFieldValue, withNameSpace]
    );

    return (
        <tr className='row-table-body'>
            <td align='center'>
                <Checkbox checked={isSelected} onChange={onSelectRow} color='primary' />
            </td>
            <td align='center'>
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
                    <SelectFile
                        name={withNameSpace("file")}
                        fileProp={item?.file}
                        showPreview={true}
                        showDowload={true}
                        showDelete={true}
                        showName={false}
                        handleAffterSubmit={handleAffterSubmitFile}
                        maxFileSize={5242880}
                    />
                </div>
            </td>
            <td align='center'>{index + 1}</td>
            <td>
                <span style={{ margin: "4px 10px", display: "block" }}>
                    {item?.documentItem?.name}
                    {item?.documentItem?.isRequired && <span style={{ color: "red" }}>*</span>}
                </span>
            </td>
            <td>
                <span style={{ margin: "4px 10px", display: "block" }}>{item?.documentItem?.description}</span>
            </td>
            <td>
                {item?.file ? (
                    <span style={{ margin: "4px 10px", display: "block" }}>
                        {item?.file?.name} - {bytesToKB(item?.file?.contentSize)}
                    </span>
                ) : item?.documentItem?.isRequired ? (
                    <span style={{ color: "red", fontSize: "12px", margin: "4px 10px", display: "block" }}>
                        * Cần bổ sung
                    </span>
                ) : null}
            </td>

            <td>
                <GlobitsDateTimePicker
                    name={withNameSpace("submissionDate")}
                    maxDate={new Date()}
                    maxDateMessage='Ngày nộp không được lớn hơn ngày hiện tại'
                    onChange={(date) => {
                        item.submissionDate = date;
                        setFieldValue(withNameSpace("submissionDate"), date);
                    }}
                    value={item?.submissionDate}
                />
            </td>
            <td align='center'>
                <GlobitsCheckBox
                    name={withNameSpace("isSubmitted")}
                    style={{ justifyContent: "center", alignItems: "center", margin: 0 }}
                    onChange={(e) => {
                        item.isSubmitted = e.target.checked;
                        setFieldValue(withNameSpace("isSubmitted"), e.target.checked);
                    }}
                    checked={item.isSubmitted}
                />
            </td>
            <td align='center'>
                {item?.documentItem?.isRequired ? (
                    <CheckIcon
                        fontSize='small'
                        style={{ color: "green", justifyContent: "center", alignItems: "center", margin: 0 }}
                    />
                ) : null}
            </td>
        </tr>
    );
});

export default memo(observer(StaffDocumentItemRow));

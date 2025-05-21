import React from "react";
import {Button} from "@material-ui/core";
import {getIn, useFormikContext} from "formik";
import GlobitsQuillEditor from "../../../../common/form/GlobitsQuillEditor";

export default function EditMailPopup({open, handleClose, handleSave, name}) {
    const {values, setFieldValue} = useFormikContext();
    const [oldContent, setOldContent] = React.useState(getIn(values, name));
    return (
        <>
            <GlobitsQuillEditor
                name={name}
                placeholder="Nhập nội dung email..."
            />
            <div className="flex flex-end gap-8">
                <Button variant="contained" color="primary" onClick={handleSave}>
                    Lưu nội dung
                </Button>
                <Button variant="contained" onClick={() => {
                    setFieldValue(name, oldContent);
                    handleClose(false)
                }}>
                    Đóng
                </Button>
            </div>
        </>
    );
}

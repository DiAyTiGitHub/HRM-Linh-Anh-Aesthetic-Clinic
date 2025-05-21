import React, { memo, useState } from "react";
import { Button, Grid, DialogContent } from "@material-ui/core";
import MessageIcon from "@material-ui/icons/Message";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsEditor from "app/common/form/GlobitsEditor";
import { useTranslation } from "react-i18next";
import IntegratedTaskHistoryV2 from "./IntegratedTaskHistoryV2";
import { toast } from "react-toastify";
import { useFormikContext } from "formik";
import SendIcon from '@material-ui/icons/Send';

toast.configure({
    autoClose: 4000,
});

function HistoryAndCommentSection() {

    const { values, setFieldValue } = useFormikContext();

    const { t } = useTranslation();

    const { taskStore, taskHistoryStore } = useStore();

    const {
        createHistoryComment,
        isContentEmpty
    } = taskHistoryStore;


    const [isDisableSendButton, setIsDisableSendButton] = useState(false);
    async function handleSaveComment() {
        setIsDisableSendButton(true);

        const comment = {
            id: values?.id,
            comment: values?.comment
        };
        if (!comment?.comment || comment?.comment?.length == 0 || comment?.comment?.trim() == "" || isContentEmpty(comment?.comment)) {
            toast.info("Bình luận gửi đi trống, vui lòng nhập nội dung...");
        }
        else if (!comment?.id || comment?.id?.length == 0) {
            toast.info("Công việc đang có lỗi, không thể gửi bình luận...");
        }
        else {
            const toastId = toast.loading("Bình luận gửi đi đang được xử lí...");
            await createHistoryComment(values);
            toast.update(toastId, { render: "Bình luận đã được gửi!", type: "success", isLoading: false, autoClose: 2000 });
        }

        setIsDisableSendButton(false);
        setFieldValue("comment", null)
    }

    return (
        <>
            <Grid container spacing={2}>
                <IntegratedTaskHistoryV2 />
            </Grid>

            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <div className="flex justify-left pt-8">
                        <MessageIcon className="mr-8" /> {t("task.comment")}:
                    </div>
                </Grid>

                <Grid item xs={12} className="pt-0">
                    <GlobitsEditor
                        name="comment"
                        placeholder="Để lại lời bình luận..."
                    />
                    <div className="commentAction w-100 flex justify-end pt-6">
                        <Button
                            variant="contained"
                            className="bgc-dark-green text-white py-4 px-12"
                            startIcon={<SendIcon />}
                            disabled={isDisableSendButton}
                            onClick={handleSaveComment}
                        >
                            Gửi bình luận
                        </Button>
                    </div>
                </Grid>
            </Grid>
        </>
    );
}

export default memo(observer(HistoryAndCommentSection));
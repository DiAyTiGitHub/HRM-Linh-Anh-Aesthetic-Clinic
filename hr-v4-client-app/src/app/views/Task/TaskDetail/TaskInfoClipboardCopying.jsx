import React, { memo, useMemo, useState } from "react";
import { Form, Formik, useFormikContext } from "formik";
import { Button, Grid, DialogContent, Tooltip, IconButton, Collapse } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import FileCopyIcon from '@material-ui/icons/FileCopyOutlined';

function TaskInfoClipboardCopying() {
    const { t } = useTranslation();

    const [copyClipboardContent, setClipboardContent] = useState("Ấn để sao chép mã công việc");

    async function handleCopyToClipboard() {
        try {
            // Select the text content of the target element
            const targetElement = document.getElementById("toCopyElement");
            if (!targetElement) {
                throw new Error(`Element with id 'toCopyElement' not found`);
            }

            const selection = window.getSelection();
            const range = document.createRange();
            range.selectNodeContents(targetElement);
            selection.removeAllRanges();
            selection.addRange(range);

            // Execute the copy command
            document.execCommand('copy');

            // Clean up selection
            selection.removeAllRanges();

            // Optionally, you can give feedback to the user that the content was copied
            setClipboardContent("Mã công việc đã được sao chép");
            // Optionally, reset the success state after a few seconds
            setTimeout(() => {
                setClipboardContent("Ấn để sao chép mã công việc");
            }, 3000); // Reset after 3 seconds
        } catch (err) {
            // Handle errors, e.g., element not found
            console.error('Failed to copy:', err);
            setClipboardContent("Sao chép mã công việc có lỗi, vui lòng thử lại sau");

        }
    }

    return (
        <Tooltip title={copyClipboardContent} placement="left">
            <IconButton className="p-0 text-dark-green"
                aria-label="search"
                onClick={handleCopyToClipboard}

            >
                <FileCopyIcon style={{ fontSize: "16px" }} />
            </IconButton>
        </Tooltip>
    );
}

export default memo(observer(TaskInfoClipboardCopying));

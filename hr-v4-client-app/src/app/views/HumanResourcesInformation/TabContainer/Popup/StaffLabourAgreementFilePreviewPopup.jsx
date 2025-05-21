import React, { useState, useEffect, memo } from 'react';
import { toast } from 'react-toastify';
import { previewFile } from '../../StaffLabourAgreementAttachmentService';

function StaffLabourAgreementFilePreviewPopup(props) {
    const { file, handleClose } = props;
    const [fileURL, setFileURL] = useState(null);
    const [fileType, setFileType] = useState('');

    // Function to create an object URL from a Blob
    function createObjectURL(blob) {
        if (!(blob instanceof Blob)) {
            console.error('Expected a Blob object, but got', blob);
            throw new TypeError('Expected a Blob object');
        }
        return URL.createObjectURL(blob);
    }

    // Function to fetch and set file URL
    async function fetchFile() {
        if (!file?.id) {
            toast.error("Invalid file ID");
            handleClose();
            return;
        }

        try {
            const blob = await previewFile(file?.id);

            if (blob && blob instanceof Blob) {
                const url = createObjectURL(blob);
                setFileURL(url);
                setFileType(blob.type); // Get the MIME type of the file
            } else {
                toast.error("Có lỗi xảy ra khi xem trước tệp này, vui lòng thử lại sau");
                handleClose();
            }
        } catch (error) {
            console.error("Failed to open file:", error);
            toast.error("Có lỗi xảy ra khi xem trước tệp này, vui lòng thử lại sau");
            handleClose();
        }
    }

    useEffect(() => {
        fetchFile();

        // Clean up the object URL when the component unmounts
        return () => {
            if (fileURL) {
                URL.revokeObjectURL(fileURL);
            }
        };
    }, [file?.id]);

    // Render different previews based on file type
    const renderPreview = () => {
        switch (true) {
            case fileType.startsWith('image/'):
                return <img src={fileURL} alt="File preview" style={{ width: '100%', height: 'auto' }} />;
            case fileType.startsWith('application/pdf'):
                return (
                    <iframe
                        // srcDoc={createHtmlDocument()}
                        src={fileURL}
                        style={{ width: '100%', height: '80vh' }}
                        title={file?.name}
                        name={file?.name}
                        id={file?.name}
                        download={file?.name}
                    />
                );
            case fileType.startsWith('text/'):
                return (
                    <iframe
                        src={fileURL}
                        style={{ width: '100%', height: '86vh' }}
                        title="Text file preview"
                    />
                );
            case fileType.startsWith('application/vnd.openxmlformats-officedocument.wordprocessingml.document'):
            case fileType.startsWith('application/msword'):
            case fileType.startsWith('application/vnd.openxmlformats-officedocument.presentationml.presentation'):
            case fileType.startsWith('application/vnd.ms-powerpoint'):
            case fileType.startsWith('application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'):
            case fileType.startsWith('application/vnd.ms-excel'):
                return (
                    <div>
                        <p>Preview not available for this file type.</p>
                        <a href={fileURL} download>Download the file</a>
                    </div>
                );
            default:
                return <p>Unsupported file type</p>;
        }
    };

    function updateIframeTitle(newTitle) {
        var iframe = document.getElementById('pdfIframe');
        iframe.title = newTitle;
    }

    // Example usage: Change the title after the iframe has loaded
    window.onload = function () {
        updateIframeTitle('New Title for PDF Preview');
    };

    return (
        <div>
            {fileURL ? (
                renderPreview()
            ) : (
                <p>Loading...</p>
            )}
        </div>
    );
}

export default memo(StaffLabourAgreementFilePreviewPopup);

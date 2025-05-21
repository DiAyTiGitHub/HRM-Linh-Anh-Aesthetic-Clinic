import React, { useMemo } from 'react';

function Print({ id, children, style }) {
    return (
        <section id={id} hidden>
            <style
                type="text/css"
                dangerouslySetInnerHTML={{
                    __html: `
                    @media print {
                        * {
                            font-family: Times New Roman;
                            margin: 0;
                            padding: 0;
                            -moz-print-color-adjust: exact;
                            -ms-print-color-adjust: exact;
                            -webkit-print-color-adjust: exact !important;
                        }

                        .radio-print-label,
                        .checked-print-label {
                            display: flex;
                            align-items: center;
                            width: 100%;
                            gap: 2px;
                        }

                        .radio-print {
                            width: 10px;
                            height: 10px;
                            border-radius: 50%;
                            box-sizing: border-box; 
                            border: 1px solid #000000;
                            padding: 1px;
                        }

                        .checked-print {
                            width: 10px;
                            height: 10px;
                            box-sizing: border-box;
                            border: 1.5px solid #000000;
                            font-size: 100%;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                        }

                        p,
                        label {
                            font-size: 12px;
                            margin: 1px 0;
                        }

                        table {
                            width: 100%;
                            border-collapse: collapse;
                        }

                        td,
                        th {
                            border: 1px solid #222;
                            box-shadow: none;
                            padding: 2pt 4pt;
                            font-size: 12px;
                            width: max-content;
                        }

                        #page_1 {
                            width: calc(100% - 1px);
                        }

                        .inline-block {
                            display: inline-block;
                        }
                        .flex {
                            display: flex;
                        }

                        .flex-column {
                            display: flex;
                            flex-direction: column;
                        }

                        .grid {
                            width: 100%;
                            display: grid;
                            grid-template-columns: repeat(12, minmax(0, 1fr));
                            box-sizing: border-box;
                        }

                        .justify-around {
                            justify-content: space-around;
                        }

                        .justify-between {
                            justify-content: space-between;
                        }

                        .justify-end {
                            justify-content: flex-end;
                        }

                        .justify-start {
                            justify-content: flex-start;
                        }

                        .justify-center {
                            justify-content: center;
                        }

                        .justify-evenly {
                            justify-content: space-evenly;
                        }

                        .align-center {
                            align-items: center;
                        }

                        .align-start {
                            align-items: start;
                        }

                        .items-end {
                            align-items: end;
                        }

                        .flex-wrap {
                            flex-wrap: flex-wrap;
                        }

                        .text-end {
                            text-align: end;
                        }

                        .text-left {
                            text-align: left;
                        }

                        .text-center {
                            text-align: center;
                        }

                        .direction-column {
                            flex-direction: column;
                        }

                        .flex-wrap {
                            flex-wrap: wrap;
                        }

                        tr,
                        .page-break-inside {
                            page-break-inside: avoid; 
                        }

                        ${useMemo(() => Array.from({ length: 12 }).map((_, index) => (
                            `
                            .col-${index + 1} {
                                grid-column: span ${index + 1};
                            }
                            `
                        )).join(" "), [])}

                        ${useMemo(() => Array.from({ length: 51 }).map((_, index) => (
                            `
                            .mr-${index}, .m-${index} {
                                margin-right: ${index}px;
                            }   
                            .ml-${index}, .m-${index} {
                                margin-left: ${index}px;
                            }  
                            .mt-${index}, .m-${index} {
                                margin-top: ${index}px;
                            }   
                            .mb-${index}, .m-${index} {
                                margin-bottom: ${index}px;
                            }
                            .pr-${index}, .p-${index} {
                                padding-right: ${index}px;
                            }   
                            .pl-${index}, .p-${index} {
                                padding-left: ${index}px;
                            }  
                            .pt-${index}, .p-${index} {
                                padding-top: ${index}px;
                            }   
                            .pb-${index}, .p-${index} {
                                padding-bottom: ${index}px;
                            }
                            .pr-${index}pt, .p-${index}pt {
                                padding-right: ${index}pt;
                            }   
                            .pl-${index}pt, .p-${index}pt {
                                padding-left: ${index}pt;
                            }  
                            .pt-${index}pt, .p-${index}pt {
                                padding-top: ${index}pt;
                            }   
                            .pb-${index}pt, .p-${index}pt {
                                padding-bottom: ${index}pt;
                            }
                            .size-${index} {
                                font-size: ${index}px;
                            }
                            .gap-${index} {
                                gap: ${index}px;
                            }
                            .h-${index} {
                                height: ${index}px;
                            }
                            .b-${index}pt {
                                border: ${index}pt solid #000;
                            }
                            .b-b-w${index}pt {
                                border-bottom-width: ${index}pt;
                            }
                            .b-t-w${index}pt {
                                border-top-width: ${index}pt;
                            }
                            `
                        )).join(" "), [])}

                        .h-full {
                            height: 100%;
                        }

                        .m-h-full {
                            min-height: 100%;
                        }

                        .w-100% {
                            width: 100%;
                        }

                        .w-33% {
                            width: 33%;
                        }

                        .w-55% {
                            width: 55%;
                        } 

                        .w-content {
                            width: max-content;
                        }

                        .whitespace-nowrap {
                            white-space: nowrap;
                        }

                        .whitespace-pre-wrap {
                            white-space: pre-wrap;
                        }

                        .bg-gray {
                            background-color: #D9D9D9;
                        }

                        @page { 
                            margin: 8mm;
                        }
                        ${style}
                    }
                    `
                }}
            />
            <section id="page_1">
                {children}
            </section>
        </section>
    )
}

export default Print;

export const ItemCheckedPrint = ({ checked, label, className = "" }) => (
    <label className={`checked-print-label ${className}`}>
        <span className="checked-print">
            <svg viewBox="0 0 384 512" width="100%" height="100%">
                <path fill={checked ? "#000000" : "transparent"} d="M376.6 84.5c11.3-13.6 9.5-33.8-4.1-45.1s-33.8-9.5-45.1 4.1L192 206 56.6 43.5C45.3 29.9 25.1 28.1 11.5 39.4S-3.9 70.9 7.4 84.5L150.3 256 7.4 427.5c-11.3 13.6-9.5 33.8 4.1 45.1s33.8 9.5 45.1-4.1L192 306 327.4 468.5c11.3 13.6 31.5 15.4 45.1 4.1s15.4-31.5 4.1-45.1L233.7 256 376.6 84.5z" />
            </svg>
        </span>
        {label}
    </label>
);

export const ItemRadioPrint = ({ checked, label, className = "" }) => (
    <label className={`radio-print-label ${className}`}>
        <span className="radio-print">
            <svg viewBox="0 0 512 512" width="100%" height="100%">
                <path d="M256 512A256 256 0 1 0 256 0a256 256 0 1 0 0 512z" fill={checked ? "#000000" : "transparent"} />
            </svg>
        </span>
        {label}
    </label>
);


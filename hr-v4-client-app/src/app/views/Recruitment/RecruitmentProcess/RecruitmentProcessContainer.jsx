import React, { memo, useEffect, useState } from 'react';
import { observer } from 'mobx-react';
import { useStore } from 'app/stores';
import './RecruitmentProcess.scss';

function RecruitmentProcessContainer() {
    const { candidateRecruitmentRoundStore, recruitmentStore } = useStore();

    const {
        selectedRecruitment,
    } = recruitmentStore;

    const {
        tabIndex,
        setTabIndex
    } = candidateRecruitmentRoundStore;

    useEffect(function () {
        if (selectedRecruitment?.id && selectedRecruitment?.recruitmentRounds?.at(0)?.id) {
            const firstRoundId = selectedRecruitment?.recruitmentRounds?.at(0)?.id;
            setTabIndex(0, firstRoundId);
        }
        
    }, [selectedRecruitment?.id, selectedRecruitment?.recruitmentRounds?.length, selectedRecruitment?.recruitmentRounds?.at(0)?.id]);

    const nextStep = () => setTabIndex(tabIndex + 1);
    const prevStep = () => setTabIndex(tabIndex - 1);
    const totalSteps = selectedRecruitment?.recruitmentRounds?.length;

    const width = `${(100 / (totalSteps - 1)) * (tabIndex)}%`;

    return (
        <div className="main-process-container content-index br-10 px-6 pt-2">

            {
                selectedRecruitment?.recruitmentRounds?.length > 0 && (
                    <div className="step-container" style={{ '--width': width }}>
                        {selectedRecruitment?.recruitmentRounds?.map(function (recruitmentRound, index) {
                            const { name, id: recruitmentRoundId } = recruitmentRound;

                            return (
                                <div
                                    className="step-wrapper"
                                    key={index}
                                    onClick={() => setTabIndex(index, recruitmentRoundId)}
                                >
                                    <div className={`step-style ${tabIndex >= index ? 'completed' : ''}`}>
                                        {/* {tabIndex > index ? (
                                        <div className="check-mark">L</div>
                                    ) : ( */}
                                        <span className="step-count">{index + 1}</span>
                                        {/* )} */}
                                    </div>

                                    <div className="steps-label-container" style={index == 0 ? { whiteSpace: "nowrap", marginLeft: "12px" } : { whiteSpace: "nowrap" }}>
                                        <span className="step-label">{name}</span>
                                    </div>
                                </div>
                            );

                        })}
                    </div>
                )
            }


            {/* <button
                className="button-style"
                onClick={prevStep}
                disabled={tabIndex === 0}
            > 
                Previous
            </button>
            <button
                className="button-style"
                onClick={nextStep}
                disabled={tabIndex === totalSteps - 1}
            >
                Next
            </button> */}
        </div>
    );
};

export default memo(observer(RecruitmentProcessContainer));

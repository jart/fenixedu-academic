/*
 * Created on 24/Set/2003
 */

package net.sourceforge.fenixedu.applicationTier.Servico.teacher.onlineTests;

import java.util.Calendar;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.InvalidArgumentsServiceException;
import net.sourceforge.fenixedu.domain.onlineTests.Metadata;
import net.sourceforge.fenixedu.domain.onlineTests.Question;
import net.sourceforge.fenixedu.domain.onlineTests.Test;
import net.sourceforge.fenixedu.domain.onlineTests.TestQuestion;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;
import net.sourceforge.fenixedu.persistenceTier.onlineTests.IPersistentMetadata;
import net.sourceforge.fenixedu.persistenceTier.onlineTests.IPersistentQuestion;
import net.sourceforge.fenixedu.persistenceTier.onlineTests.IPersistentStudentTestQuestion;
import net.sourceforge.fenixedu.persistenceTier.onlineTests.IPersistentTestQuestion;
import net.sourceforge.fenixedu.applicationTier.IService;

/**
 * @author Susana Fernandes
 */
public class DeleteExercise implements IService {

    public void run(Integer executionCourseId, Integer metadataId) throws ExcepcaoPersistencia, InvalidArgumentsServiceException {
        ISuportePersistente persistentSuport = PersistenceSupportFactory.getDefaultPersistenceSupport();
        IPersistentMetadata persistentMetadata = persistentSuport.getIPersistentMetadata();
        Metadata metadata = (Metadata) persistentMetadata.readByOID(Metadata.class, metadataId);
        if (metadata == null)
            throw new InvalidArgumentsServiceException();
        List<Question> questionList = persistentSuport.getIPersistentQuestion().readByMetadata(metadata);
        boolean delete = true;
        IPersistentQuestion persistentQuestion = persistentSuport.getIPersistentQuestion();
        IPersistentStudentTestQuestion persistentStudentTestQuestion = persistentSuport.getIPersistentStudentTestQuestion();
        for (Question question : questionList) {
            List<TestQuestion> testQuestionList = persistentSuport.getIPersistentTestQuestion().readByQuestion(question.getIdInternal());
            for (TestQuestion testQuestion : testQuestionList) {
                removeTestQuestionFromTest(persistentSuport, testQuestion);
            }
            if (persistentStudentTestQuestion.countByQuestion(question.getIdInternal()) == 0) {
                persistentQuestion.deleteByOID(Question.class, question.getIdInternal());
            } else {
                question.setVisibility(new Boolean("false"));
                delete = false;
            }
        }
        if (delete) {
            persistentQuestion.deleteByMetadata(metadata);
            persistentMetadata.deleteByOID(Metadata.class, metadata.getIdInternal());
        } else {
            metadata.setVisibility(new Boolean("false"));
        }
    }

    private void removeTestQuestionFromTest(ISuportePersistente persistentSuport, TestQuestion testQuestion) throws ExcepcaoPersistencia {
        IPersistentTestQuestion persistentTestQuestion = persistentSuport.getIPersistentTestQuestion();

        Test test = (Test) persistentSuport.getIPersistentTest().readByOID(Test.class, testQuestion.getKeyTest());
        if (test == null)
            throw new ExcepcaoPersistencia();
        List<TestQuestion> testQuestionList = persistentTestQuestion.readByTest(test.getIdInternal());
        Integer questionOrder = testQuestion.getTestQuestionOrder();
        if (testQuestionList != null) {
            for (TestQuestion iterTestQuestion : testQuestionList) {
                Integer iterQuestionOrder = iterTestQuestion.getTestQuestionOrder();
                if (questionOrder.compareTo(iterQuestionOrder) <= 0) {
                    iterTestQuestion.setTestQuestionOrder(new Integer(iterQuestionOrder.intValue() - 1));
                }
            }
        }
        persistentTestQuestion.deleteByOID(TestQuestion.class, testQuestion.getIdInternal());
        test.setLastModifiedDate(Calendar.getInstance().getTime());
    }
}
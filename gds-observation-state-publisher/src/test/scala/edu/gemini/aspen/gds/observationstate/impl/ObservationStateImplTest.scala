package edu.gemini.aspen.gds.observationstate.impl

import org.junit.Assert._
import org.mockito.Mockito._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.CollectionError
import collection.immutable.Set
import java.util.concurrent.TimeUnit
import edu.gemini.aspen.gds.api.fits.FitsKeyword

class ObservationStateImplTest {

  @Test
  def testObsInProgress() {
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
    assertTrue(obsState.getObservationsInProgress.isEmpty)

    obsState.startObservation("label1")
    assertEquals(new DataLabel("label1"), obsState.getObservationsInProgress.head)
    assertEquals(obsState.getObservationsInProgress.size, 1)

    obsState.startObservation("label2")
    obsState.endObservation("label2")
    assertEquals(new DataLabel("label1"), obsState.getObservationsInProgress.head)
    assertEquals(obsState.getObservationsInProgress.size, 1)
  }

  @Test
  def testLastDataLabel() {
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
    obsState.startObservation("label1")
    obsState.startObservation("label2")
    obsState.endObservation("label2")
    assertEquals(Some(new DataLabel("label2")), obsState.getLastDataLabel)

    obsState.endObservation("label1")
    assertEquals(Some(new DataLabel("label2")), obsState.getLastDataLabel)
  }

  @Test
  def testLastDataLabels() {
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
    obsState.startObservation("label1")
    obsState.startObservation("label2")
    obsState.endObservation("label2")
    assertEquals(new DataLabel("label2") :: new DataLabel("label1") :: Nil, obsState.getLastDataLabel(2))

    obsState.endObservation("label1")
    assertEquals(new DataLabel("label2") :: new DataLabel("label1") :: Nil, obsState.getLastDataLabel(2))

    obsState.startObservation("label3")
    obsState.endObservation("label3")
    assertEquals(new DataLabel("label3") :: new DataLabel("label2") :: Nil, obsState.getLastDataLabel(2))
  }

  @Test
  def testMissing() {
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
    obsState.startObservation("label1")
    obsState.registerMissingKeyword("label1", FitsKeyword("A") :: Nil)
    obsState.registerMissingKeyword("label1", FitsKeyword("B") :: Nil)
    assertEquals((FitsKeyword("B") :: FitsKeyword("A") :: Nil).toSet, obsState.getMissingKeywords("label1").toSet)
  }

  @Test
  def testError() {
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
    obsState.startObservation("label1")
    obsState.registerCollectionError("label1", List((FitsKeyword("A"), CollectionError.GenericError)))
    obsState.registerCollectionError("label1", List((FitsKeyword("B"), CollectionError.GenericError)))
    assertEquals(Set((FitsKeyword("B"), CollectionError.GenericError), (FitsKeyword("A"), CollectionError.GenericError)), obsState.getKeywordsInError("label1").toSet)
  }

  @Test
  def testInError() {
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
    assertFalse(obsState.isInError("label1"))
    obsState.startObservation("label1")
    assertFalse(obsState.isInError("label1"))
    obsState.registerCollectionError("label1", List((FitsKeyword("A"), CollectionError.GenericError)))
    assertTrue(obsState.isInError("label1"))
  }

  @Test
  def testExpiration() {
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher])) {
      override def expirationMillis = 5
    }
    obsState.startObservation("label1")
    obsState.registerCollectionError("label1", List((FitsKeyword("A"), CollectionError.GenericError)))
    obsState.registerCollectionError("label1", List((FitsKeyword("B"), CollectionError.GenericError)))

    // Verify it is there
    assertEquals(Set((FitsKeyword("B"), CollectionError.GenericError), (FitsKeyword("A"), CollectionError.GenericError)), obsState.getKeywordsInError("label1").toSet)

    TimeUnit.MILLISECONDS.sleep(5)

    // Now it is gone
    assertEquals(Set(), obsState.getKeywordsInError("label1").toSet)
  }
}
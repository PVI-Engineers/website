import cityNetwork from '../assets/media-city-network.svg'
import civilCorridor from '../assets/media-civil-corridor.svg'
import contactOffice from '../assets/media-contact-office.svg'
import documentsDesk from '../assets/media-documents-desk.svg'
import infrastructureHero from '../assets/media-infrastructure-hero.svg'
import newsEvents from '../assets/media-news-events.svg'
import privacyShield from '../assets/media-privacy-shield.svg'
import surveyGrid from '../assets/media-survey-grid.svg'
import teamOffice from '../assets/media-team-office.svg'
import waterPlant from '../assets/media-water-plant.svg'
import workShowcase from '../assets/media-work-showcase.svg'

const defaultMedia = {
  hero: infrastructureHero,
  gallery: [infrastructureHero, workShowcase, cityNetwork],
}

const sectionMedia = {
  '/civil': {
    hero: civilCorridor,
    gallery: [civilCorridor, infrastructureHero, cityNetwork],
  },
  '/survey': {
    hero: surveyGrid,
    gallery: [surveyGrid, cityNetwork, infrastructureHero],
  },
  '/water-wastewater': {
    hero: waterPlant,
    gallery: [waterPlant, infrastructureHero, workShowcase],
  },
  '/bidding-documents': {
    hero: documentsDesk,
    gallery: [documentsDesk, workShowcase, civilCorridor],
  },
  '/about-us': {
    hero: teamOffice,
    gallery: [teamOffice, cityNetwork, workShowcase],
  },
  '/locations': {
    hero: cityNetwork,
    gallery: [cityNetwork, infrastructureHero, teamOffice],
  },
  '/news-and-events': {
    hero: newsEvents,
    gallery: [newsEvents, workShowcase, teamOffice],
  },
  '/careers': {
    hero: teamOffice,
    gallery: [teamOffice, newsEvents, civilCorridor],
  },
  '/contact': {
    hero: contactOffice,
    gallery: [contactOffice, teamOffice, cityNetwork],
  },
  '/our-work': {
    hero: workShowcase,
    gallery: [workShowcase, civilCorridor, waterPlant],
  },
  '/privacy-statement': {
    hero: privacyShield,
    gallery: [privacyShield, documentsDesk, contactOffice],
  },
}

function getSectionMedia(sectionPath) {
  return sectionMedia[sectionPath] ?? defaultMedia
}

const homeHeroImage = infrastructureHero

const homeWorkGallery = [
  {
    title: 'Road and Corridor Programs',
    location: 'Urban mobility and regional highway upgrades',
    image: civilCorridor,
  },
  {
    title: 'Water and Drainage Systems',
    location: 'Integrated treatment, distribution, and resilience',
    image: waterPlant,
  },
  {
    title: 'Survey, Mapping and Documentation',
    location: 'Data-led planning and bid-ready packages',
    image: surveyGrid,
  },
]

export { getSectionMedia, homeHeroImage, homeWorkGallery }

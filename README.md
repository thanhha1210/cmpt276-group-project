**HealthAce**

Requirements and Specification Document

06/15/2024 Version 1

**Overview**

Our project aims to establish a website serving educational and practical purposes. Our platform aims to comprehensively enhance healthcare management by using various online services. Traditionally, patients go to the clinic, wait for a ticket, deal with long waiting queues, and phone receptionists, and rely on Google or word of mouth for information. They find it hard to reach healthcare locations in person due to long queues and anxiety-inducing environments. To resolve this problem, we offer a website that allows users to book appointments in advance, view their health records, provide feedback to doctors, and join doctor consultation events.

**Customer**

Our primary target audience includes patients and doctors. It allows users to manage their healthcare needs online, therefore eliminating the need for physical visits and reducing the stress associated with hospital queues.

Competitive Analysis

Our direct competitors are hospital web apps, but they do not offer as much functionality as we do. These apps provide typical services such as appointment booking and viewing health records, lacking the comprehensive features that our platform offers.

**Stories**

Patients benefit from a user-friendly interface where they can view doctors available, book appointments, and manage their bookings. When an appointment is booked, it automatically synchronizes with both patient and admin dashboards, ensuring the visibility and coordination of the system. In addition, appointments are synchronized with patients' online calendars by using Google Calendar API, which helps them manage their schedules and receive reminders about their upcoming appointments. In case when patients cancel or change their appointments, it also updates the appointment status to 'canceled' on the administrator’s dashboard for clear communication. 

After each appointment, doctors can access patient records to view medical histories, update treatment plans, and maintain continuity of care. It synchronizes with users’ dashboards, allowing them to track their health status. Besides, patients can provide feedback on their healthcare experiences, enabling administrators to facilitate continuous improvement and transparency by forwarding feedback to doctors. Other patients can observe reviews left by previous users when searching for specific doctors, empowering informed decisions about their healthcare providers. 
HealthAce also allows administrators to create and manage healthcare-related events. These events will be held in the user's forum, where they can engage in health discussions, attend virtual events, and interact with healthcare experts. 

Additionally, we plan to incorporate a medical assistant chatbot utilizing the OpenAI API to enhance patient interaction and support. Lastly, the platform's location-based service finder assists patients in locating nearby healthcare facilities using the Google Maps API, optimizing accessibility and convenience.
